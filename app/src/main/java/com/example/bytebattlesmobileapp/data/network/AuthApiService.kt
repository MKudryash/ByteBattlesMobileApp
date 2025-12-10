package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.ChangePasswordDto
import com.example.bytebattlesmobileapp.data.network.dto.auth.AuthResponse
import com.example.bytebattlesmobileapp.data.network.dto.auth.RefreshTokenRequest
import com.example.bytebattlesmobileapp.data.network.dto.auth.RegisterRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.LoginRequest

interface AuthApiService {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun refreshToken(request: RefreshTokenRequest): AuthResponse
    suspend fun logout()
    suspend fun passwordChange(request: ChangePasswordDto)
}
