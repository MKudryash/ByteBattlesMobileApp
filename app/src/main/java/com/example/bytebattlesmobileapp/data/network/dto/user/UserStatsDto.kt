package com.example.bytebattlesmobileapp.data.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserStatsDto(
    val userId: String,
    val rating: Int,
    val rank: Int,
    val battlesTotal: Int,
    val battlesWon: Int,
    val winRate: Double,
    val averageCompletionTime: Long?,
    val favoriteLanguage: String?,
    val tasksSolved: Int
)

