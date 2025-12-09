package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import com.example.bytebattlesmobileapp.data.network.dto.task.LanguageDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import com.example.bytebattlesmobileapp.di.AuthException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.UUID

class TaskApiServiceImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : TaskApiService {

    override suspend fun getTaskById(taskId: UUID): TaskDto {
        return try {
            client.get("task/$taskId").body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        }
    }

    override suspend fun getTasksWithPagination(
        page: Int,
        pageSize: Int,
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<TaskDto> {
        return try {
            client.get("task/search-paged") {
                parameter("page", page)
                parameter("pageSize", pageSize)
                searchTerm?.let { parameter("searchTerm", it) }
                difficulty?.let { parameter("difficulty", it) }
                languageId?.let { parameter("languageId", it) }
            }.body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        }
    }

    override suspend fun getTasks(
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<TaskDto> {
        return try {
            client.get("task/search") {
                searchTerm?.let { parameter("searchTerm", it) }
                difficulty?.let { parameter("difficulty", it) }
                languageId?.let { parameter("languageId", it) }
            }.body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        } catch (e: ServerResponseException) {
            // Проверяем, если это ошибка аутентификации
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        }
    }

    override suspend fun getLanguageById(languageId: UUID): LanguageDto {
        return try {
            client.get("language/$languageId").body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        }
    }

    override suspend fun getLanguages(
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<LanguageDto> {
        return try {
            client.get("language/search") {
                searchTerm?.let { parameter("searchTerm", it) }
                difficulty?.let { parameter("difficulty", it) }
                languageId?.let { parameter("languageId", it) }
            }.body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        } catch (e: ServerResponseException) {
            // Проверяем, если это ошибка аутентификации
            if (e.response.status == HttpStatusCode.Unauthorized) {
                tokenManager.clearTokens()
                throw AuthException("Authentication required")
            }
            throw e
        }
    }
}