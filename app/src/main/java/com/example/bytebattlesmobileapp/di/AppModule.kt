package com.example.bytebattlesmobileapp.di

import android.content.Context
import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import com.example.bytebattlesmobileapp.data.interceptors.AuthInterceptor
import com.example.bytebattlesmobileapp.data.network.*
import com.example.bytebattlesmobileapp.data.network.dto.auth.UUIDSerializer
import com.example.bytebattlesmobileapp.data.repository.AuthRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.TaskRepositoryImpl
import com.example.bytebattlesmobileapp.data.repository.UserRepositoryImpl
import com.example.bytebattlesmobileapp.domain.repository.*
import com.example.bytebattlesmobileapp.domain.usecase.CreateBattleRoomUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetTasksUseCase
import com.example.bytebattlesmobileapp.domain.usecase.GetUserProfileUseCase
import com.example.bytebattlesmobileapp.domain.usecase.JoinBattleRoomUseCase
import com.example.bytebattlesmobileapp.domain.usecase.LoginUseCase
import com.example.bytebattlesmobileapp.domain.usecase.RegisterUseCase
import com.wakaztahir.codeeditor.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.net.SocketTimeoutException
import java.util.UUID
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
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
        }
    }



    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        authInterceptor: AuthInterceptor
    ): HttpClient {
        return HttpClient(Android) {
            // Базовая конфигурация
            expectSuccess = true
            defaultRequest {
                url.takeFrom("https://api.m.hobbit1021.ru/api/")
                contentType(ContentType.Application.Json)
            }

            // Логирование
            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            BuildConfig.DEBUG
                            android.util.Log.d("Ktor", message)
                        }
                    }
                    level = LogLevel.ALL
                }
            }

            // JSON сериализация
            install(ContentNegotiation) {
                json(json)
            }

            // HTTP плагины
            install(HttpRequestRetry) {
                maxRetries = 3
                retryOnExceptionIf { _, cause ->
                    cause is HttpRequestTimeoutException ||
                            cause is ConnectTimeoutException ||
                            cause is SocketTimeoutException
                }
                delayMillis { retry -> retry * 1000L }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, request ->
                    if (exception is ClientRequestException &&
                        exception.response.status == HttpStatusCode.Unauthorized) {
                        // Здесь можно добавить логику обновления токена
                        // и повторного выполнения запроса
                        throw exception
                    }
                }
            }

            // Валидация ответов
            HttpResponseValidator {
                validateResponse { response ->
                    when (response.status.value) {
                        in 400..499 -> throw ClientRequestException(response, "Client error")
                        in 500..599 -> throw ServerResponseException(response, "Server error")
                    }
                }
            }
        }
    }

    /*@Provides
    @Singleton
    fun provideWebSocketClient(): HttpClient {
        return HttpClient(Android) {
            install(WebSockets)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }*/

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    // API сервисы
    @Provides
    @Singleton
    fun provideAuthApiService(client: HttpClient): AuthApiService {
        return AuthApiServiceImpl(client)
    }

/*    @Provides
    @Singleton
    fun provideBattleApiService(client: HttpClient): BattleApiService {
        return BattleApiServiceImpl(client)
    }*/

    @Provides
    @Singleton
    fun provideTaskApiService(client: HttpClient): TaskApiService {
        return TaskApiServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideUserApiService(client: HttpClient): UserApiService {
        return UserApiServiceImpl(client)
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

   /* @Provides
    @Singleton
    fun provideBattleRepository(
        battleApi: BattleApiService
    ): BattleRepository {
        return BattleRepositoryImpl(battleApi)
    }*/

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
}

// UseCase Module
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
    fun provideGetUserProfileUseCase(repository: UserRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }

   /* @Provides
    @Singleton
    fun provideCreateBattleRoomUseCase(repository: BattleRepository): CreateBattleRoomUseCase {
        return CreateBattleRoomUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideJoinBattleRoomUseCase(repository: BattleRepository): JoinBattleRoomUseCase {
        return JoinBattleRoomUseCase(repository)
    }*/

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

}