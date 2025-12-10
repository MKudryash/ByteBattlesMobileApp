package com.example.bytebattlesmobileapp.domain.model

data class Achievement(
    val id: String,
    val name: String?,
    val description: String?,
    val iconUrl: String?,
    val category: String?,
    val rarity: String?,
    val isSecret: Boolean?,
    val unlockedAt: String?,
    val progress: Int?,
)