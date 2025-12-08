package com.example.bytebattlesmobileapp.data.interceptors


import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) {
    suspend fun intercept(request: HttpRequestBuilder) {
        val token = tokenManager.getAccessToken()
        token?.let {
            request.header(HttpHeaders.Authorization, "Bearer $it")
        }
    }
}