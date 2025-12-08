package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)


@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)