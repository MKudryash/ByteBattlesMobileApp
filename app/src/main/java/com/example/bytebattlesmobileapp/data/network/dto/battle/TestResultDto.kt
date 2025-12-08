package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TestResultDto(
    @Contextual
    val testId: UUID,
    val status: String, // Passed, Failed
    val input: String,
    val expectedOutput: String,
    val actualOutput: String? = null,
    val executionTime: Long
)