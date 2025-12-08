package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CodeSubmissionRequest(
    @Contextual
    val roomId: UUID,
    @Contextual
    val taskId: UUID,
    val code: String,
    @Contextual
    val languageId: UUID
)