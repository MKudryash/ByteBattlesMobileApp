package com.example.bytebattlesmobileapp.data.network.dto

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String
)
