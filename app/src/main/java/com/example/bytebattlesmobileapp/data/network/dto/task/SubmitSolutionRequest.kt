package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubmitSolutionRequest(
    @Contextual
    val taskId: UUID,
    val code: String,
    @Contextual
    val languageId: UUID
)