package com.example.bytebattlesmobileapp.data.network

import com.wakaztahir.codeeditor.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object KtorClient {
    private const val BASE_URL = "https://api.m.hobbit1021.ru/"

    val httpClient = HttpClient(Android) {
        // Базовая конфигурация
        expectSuccess = true
        defaultRequest {
            url.takeFrom("$BASE_URL/api/")
            contentType(ContentType.Application.Json)
        }

        // Логирование
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        // JSON сериализация
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        // Таймауты
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }

        // HTTP плагины
        install(HttpSend) {
            maxSendCount = 3
        }

        // Ответ по умолчанию
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status.value) {
                    in 400..499 -> throw ClientRequestException(response, "Client error")
                    in 500..599 -> throw ServerResponseException(response, "Server error")
                }
            }
        }
    }

    // WebSocket клиент
    val webSocketClient = HttpClient(Android) {
        install(WebSockets)
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}