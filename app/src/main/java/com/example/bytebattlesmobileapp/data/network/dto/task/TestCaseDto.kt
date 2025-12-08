package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TestCaseDto(
    @Contextual
    val id: UUID,
    val input: String,
    val expectedOutput: String,
    val isPublic: Boolean
)