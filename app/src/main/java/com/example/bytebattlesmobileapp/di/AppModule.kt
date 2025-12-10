package com.example.bytebattlesmobileapp.di

import android.content.Context
import com.example.bytebattlesmobileapp.data.datasource.local.PlayerIdManager
import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import com.example.bytebattlesmobileapp.data.interceptors.AuthInterceptor
import com.example.bytebattlesmobileapp.data.network.*
import com.example.bytebattlesmobileapp.data.network.dto.auth.RefreshTokenRequest
import com.example.bytebattlesmobileapp.data.repository.AuthRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.BattleRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.SolutionRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.TaskRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.UserRepositoryImpl
import com.example.bytebattlesmobileapp.domain.repository.*
import com.example.bytebattlesmobileapp.domain.usecase.*
import com.wakaztahir.codeeditor.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    // 1. HttpClient для НЕаутентифицированных запросов (логин/регистрация/refresh)
    @Provides
    @Singleton
    @Named("unauthHttpClient")
    fun provideUnauthHttpClient(
        json: Json,
        @ApplicationContext context: Context
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            defaultRequest {
                url("https://api.m.hobbit1021.ru/api/")
                contentType(ContentType.Application.Json)
                header(HttpHeaders.UserAgent, "ByteBattles/Android")
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            android.util.Log.d("Ktor-Unauth", message)
                        }
                    }
                    level = LogLevel.ALL
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 10000L
                socketTimeoutMillis = 10000L
            }
        }
    }

    // 2. ОТДЕЛЬНЫЙ AuthApiService для обновления токена (без циклической зависимости)
    @Provides
    @Singleton
    @Named("refreshAuthApiService")
    fun provideRefreshAuthApiService(
        @Named("unauthHttpClient") client: HttpClient
    ): AuthApiService {
        return AuthApiServiceImpl(client)
    }

    // 3. HttpClient для аутентифицированных запросов (использует refreshAuthApiService)
    @Provides
    @Singleton
    @Named("authHttpClient")
    fun provideAuthHttpClient(
        json: Json,
        tokenManager: TokenManager,
        @Named("refreshAuthApiService") authApiService: AuthApiService, // Используем refreshAuthApiService
        @ApplicationContext context: Context
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            defaultRequest {
                url("https://api.m.hobbit1021.ru/api/")
                contentType(ContentType.Application.Json)
                header(HttpHeaders.UserAgent, "ByteBattles/Android")
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            android.util.Log.d("Ktor-Auth", message)
                        }
                    }
                    level = LogLevel.HEADERS
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            // Добавляем Bearer аутентификацию с обновлением токена
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = runBlocking { tokenManager.getAccessToken().firstOrNull() }
                        token?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }

                    refreshTokens {
                        // Логика обновления токена
                        try {
                            android.util.Log.d("TokenRefresh", "Attempting to refresh token...")

                            // Получаем refresh токен
                            val refreshToken = runBlocking {
                                tokenManager.getTokens()?.second
                            } ?: throw AuthException("No refresh token available")

                            // Получаем access токен
                            val accessToken = runBlocking {
                                tokenManager.getAccessToken().firstOrNull()
                            } ?: throw AuthException("No access token available")

                            // Вызываем refresh endpoint через refreshAuthApiService
                            val response = authApiService.refreshToken(
                                RefreshTokenRequest(accessToken, refreshToken)
                            )

                            // Сохраняем новые токены
                            runBlocking {
                                tokenManager.saveToken(
                                    response.accessToken,
                                    response.refreshToken
                                )
                            }

                            android.util.Log.d("TokenRefresh", "Token refreshed successfully")

                            response.refreshToken?.let {
                                BearerTokens(
                                    accessToken = response.accessToken,
                                    refreshToken = it
                                )
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("TokenRefresh", "Failed to refresh token", e)
                            // Очищаем токены и выбрасываем исключение
                            runBlocking {
                                tokenManager.clearTokens()
                            }
                            throw AuthException("Token refresh failed")
                        }
                    }

                    // Для каких эндпоинтов НЕ отправлять токен
                    sendWithoutRequest { request ->
                        request.url.encodedPath.contains("/auth/") ||
                                request.url.encodedPath.contains("/register") ||
                                request.url.encodedPath.contains("/public/")
                    }
                }
            }

            // Обработчик ответов для автоматической обработки 401
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    when (statusCode) {
                        401 -> {
                            android.util.Log.d(
                                "AuthHttpClient",
                                "Received 401, triggering token refresh"
                            )
                        }

                        in 400..499 -> throw ClientRequestException(
                            response,
                            "Client error: $statusCode"
                        )

                        in 500..599 -> throw ServerResponseException(
                            response,
                            "Server error: $statusCode"
                        )
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 10000L
                socketTimeoutMillis = 10000L
            }

            // Добавляем заголовок авторизации по умолчанию
            install(DefaultRequest) {
                val token = runBlocking { tokenManager.getAccessToken().firstOrNull() }
                token?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
        }
    }

    // 4. HttpClient для WebSocket (также использует refreshAuthApiService)
    @Provides
    @Singleton
    @Named("webSocketClient")
    fun provideWebSocketClient(
        json: Json,
        tokenManager: TokenManager,
        @Named("refreshAuthApiService") authApiService: AuthApiService // Используем refreshAuthApiService
    ): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets) {
                maxFrameSize = Long.MAX_VALUE
                pingInterval = 20_000
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 10_000
            }

            install(ContentNegotiation) {
                json(json)
            }

            // Для WebSocket также добавляем логику обновления токена
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = runBlocking { tokenManager.getAccessToken().firstOrNull() }
                        token?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }

                    refreshTokens {
                        try {
                            val refreshToken = runBlocking {
                                tokenManager.getTokens()?.second
                            } ?: throw AuthException("No refresh token")

                            val accessToken = runBlocking {
                                tokenManager.getAccessToken().firstOrNull()
                            } ?: throw AuthException("No access token available")

                            val response = authApiService.refreshToken(
                                RefreshTokenRequest(accessToken, refreshToken)
                            )

                            runBlocking {
                                tokenManager.saveToken(
                                    response.accessToken,
                                    response.refreshToken
                                )
                            }

                            response.refreshToken?.let {
                                BearerTokens(
                                    accessToken = response.accessToken,
                                    refreshToken = it
                                )
                            }
                        } catch (e: Exception) {
                            runBlocking { tokenManager.clearTokens() }
                            throw AuthException("WebSocket token refresh failed")
                        }
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            android.util.Log.d("Ktor-WebSocket", message)
                        }
                    }
                    level = LogLevel.HEADERS
                }
            }
        }
    }

    @Provides
    @Singleton
    fun providePlayerIdManager(@ApplicationContext context: Context): PlayerIdManager {
        return PlayerIdManager(context)
    }

    // 5. ОСНОВНОЙ AuthApiService (для логина/регистрации, использует authHttpClient)
    @Provides
    @Singleton
    fun provideAuthApiService(
        @Named("authHttpClient") client: HttpClient
    ): AuthApiService {
        return AuthApiServiceImpl(client)
    }

    // Другие API сервисы
    @Provides
    @Singleton
    fun provideTaskApiService(
        @Named("authHttpClient") client: HttpClient,
        tokenManager: TokenManager
    ): TaskApiService {
        return TaskApiServiceImpl(client, tokenManager)
    }

    @Provides
    @Singleton
    fun provideUserApiService(
        @Named("authHttpClient") client: HttpClient
    ): UserApiService {
        return UserApiServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideSolutionApiService(
        @Named("authHttpClient") client: HttpClient
    ): SolutionApiService {
        return SolutionApiServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideBattleApiService(
        @Named("webSocketClient") client: HttpClient,
        json: Json,
        tokenManager: TokenManager
    ): BattleApiService {
        return BattleApiServiceImpl(client, json, tokenManager)
    }

    // Репозитории
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApiService, // основной authApi
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, tokenManager)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskApi: TaskApiService
    ): TaskRepository {
        return TaskRepositoryImpl(taskApi)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApiService
    ): UserRepository {
        return UserRepositoryImpl(userApi)
    }

    @Provides
    @Singleton
    fun provideSolutionRepository(
        solutionApi: SolutionApiService
    ): SolutionRepository {
        return SolutionRepositoryImpl(solutionApi)
    }

    @Provides
    @Singleton
    fun provideBattleRepository(
        battleApi: BattleApiService
    ): BattleRepository {
        return BattleRepositoryImpl(battleApi)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenUseCase(repository: AuthRepository): RefreshTokenUseCase {
        return RefreshTokenUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideChangePasswordUseCase(repository: AuthRepository): ChangePasswordUseCase {
        return ChangePasswordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTaskByIdUseCase(repository: TaskRepository): GetTaskByIdUseCase {
        return GetTaskByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLanguageByIdUseCase(repository: TaskRepository): GetLanguageByIdUseCase {
        return GetLanguageByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLanguagesUseCase(repository: TaskRepository): GetLanguagesUseCase {
        return GetLanguagesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTasksWithPaginationUseCase(repository: TaskRepository): GetTasksWithPaginationUseCase {
        return GetTasksWithPaginationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLeaderBordUseCase(repository: UserRepository): GetLeaderBordUseCase {
        return GetLeaderBordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserProfileUseCaseCase(repository: UserRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(repository: UserRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserActivitiesUseCase(repository: UserRepository): GetUserActivitiesUseCase {
        return GetUserActivitiesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserAchievementsUseCase(repository: UserRepository): GetUserAchievementsUseCase {
        return GetUserAchievementsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providerSubmitSolutionUseCase(repository: SolutionRepository): SubmitSolutionUseCase {
        return SubmitSolutionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideConnectBattleUseCase(repository: BattleRepository): ConnectBattleUseCase {
        return ConnectBattleUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDisconnectBattleUseCase(repository: BattleRepository): DisconnectBattleUseCase {
        return DisconnectBattleUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateRoomUseCase(repository: BattleRepository): CreateRoomUseCase {
        return CreateRoomUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideJoinRoomUseCase(repository: BattleRepository): JoinRoomUseCase {
        return JoinRoomUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleReadyUseCase(repository: BattleRepository): ToggleReadyUseCase {
        return ToggleReadyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSubmitCodeUseCase(repository: BattleRepository): SubmitCodeUseCase {
        return SubmitCodeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLeaveRoomUseCase(repository: BattleRepository): LeaveRoomUseCase {
        return LeaveRoomUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBattleMessagesUseCase(repository: BattleRepository): GetBattleMessagesUseCase {
        return GetBattleMessagesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBattleConnectionStateUseCase(repository: BattleRepository): GetBattleConnectionStateUseCase {
        return GetBattleConnectionStateUseCase(repository)
    }

}


class AuthException(message: String) : Exception(message)
class RefreshTokenException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)