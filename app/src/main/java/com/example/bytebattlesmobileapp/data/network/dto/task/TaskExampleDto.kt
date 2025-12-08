package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskExampleDto(
    val input: String,
    val output: String,
    val explanation: String? = null
)