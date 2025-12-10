package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.auth.AuthResponse
import com.example.bytebattlesmobileapp.data.network.dto.auth.RefreshTokenRequest
import com.example.bytebattlesmobileapp.data.network.dto.auth.RegisterRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

// Реализации API
class AuthApiServiceImpl(private val client: HttpClient) : AuthApiService {
    override suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            setBody(request)
        }.body()
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("auth/register") {
            setBody(request)
        }.body()
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        return client.post("auth/refresh-token") {
            setBody(request)
        }.body()
    }

    override suspend fun logout() {
        client.post("auth/logout")
    }
}
