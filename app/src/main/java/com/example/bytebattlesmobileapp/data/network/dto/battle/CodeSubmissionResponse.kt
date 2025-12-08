package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CodeSubmissionResponse(
    @Contextual
    val submissionId: UUID,
    val status: String, // Pending, Running, Success, Failed
    val testResults: List<TestResultDto>? = null,
    val executionTime: Long? = null,
    val submittedAt: String
)