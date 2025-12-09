package com.example.bytebattlesmobileapp.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivitiesDto (
    val type: String?,
    val title: String?,
    val description: String?,
    val timestamp: String?,
    val experienceGained: Int?,
)
