package com.example.bytebattlesmobileapp.data.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String,
    val email: String,
    val rating: Int,
    val battlesWon: Int,
    val battlesLost: Int,
    val tasksSolved: Int,
    val createdAt: String,
    val lastActiveAt: String? = null
)