package com.example.bytebattlesmobileapp.data.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val accessToken: String,
    val refreshToken: String
)