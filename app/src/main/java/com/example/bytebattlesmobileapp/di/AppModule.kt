package com.example.bytebattlesmobileapp.di

import android.content.Context
import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import com.example.bytebattlesmobileapp.data.interceptors.AuthInterceptor
import com.example.bytebattlesmobileapp.data.network.*
import com.example.bytebattlesmobileapp.data.repository.AuthRepositoryImpl
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
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
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

    // HttpClient для аутентификационных запросов (логин/регистрация)
    @Provides
    @Singleton
    @Named("authHttpClient")
    fun provideAuthHttpClient(
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
                            android.util.Log.d("Ktor-Auth", message)
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

    // HttpClient для аутентифицированных запросов (с Bearer токеном)
    @Provides
    @Singleton
    @Named("authenticatedHttpClient")
    fun provideAuthenticatedHttpClient(
        json: Json,
        tokenManager: TokenManager,
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
                            android.util.Log.d("Ktor-Authenticated", message)
                        }
                    }
                    level = LogLevel.HEADERS
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            // Плагин Auth с Bearer токеном
            install(Auth) {
                bearer {
                    loadTokens {
                        // Получаем токен
                        val token = tokenManager.getAccessToken().firstOrNull()
                        token?.let {
                            val tokens = tokenManager.getTokens()
                            val refreshToken = tokens?.second ?: it
                            BearerTokens(it, refreshToken)
                        }
                    }

                    refreshTokens {
                        // Реализация обновления токена
                        val tokens = tokenManager.getTokens()
                        if (tokens != null) {
                            // Здесь можно вызвать API для обновления токена
                            BearerTokens(tokens.first, tokens.second)
                        } else {
                            null
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 10000L
                socketTimeoutMillis = 10000L
            }
        }
    }

    // API сервисы - ВАЖНО: исправьте эти методы
    @Provides
    @Singleton
    fun provideAuthApiService(
        @Named("authHttpClient") client: HttpClient  // Добавьте @Named аннотацию
    ): AuthApiService {
        return AuthApiServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideTaskApiService(
        @Named("authenticatedHttpClient") client: HttpClient,  // Добавьте @Named аннотацию
        tokenManager: TokenManager
    ): TaskApiService {
        return TaskApiServiceImpl(client, tokenManager)
    }

    @Provides
    @Singleton
    fun provideUserApiService(
        @Named("authenticatedHttpClient") client: HttpClient  // Добавьте @Named аннотацию
    ): UserApiService {
        return UserApiServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideSolutionApiService(
        @Named("authenticatedHttpClient") client: HttpClient  // Добавьте @Named аннотацию
    ): SolutionApiService {
        return SolutionApiServiceImpl(client)
    }

    // Репозитории
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApiService,
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
    fun providerSubmitSolutionUseCase(repository: SolutionRepository): SubmitSolutionUseCase {
        return SubmitSolutionUseCase(repository)
    }


}

class AuthException(message: String) : Exception(message)
class RefreshTokenException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)