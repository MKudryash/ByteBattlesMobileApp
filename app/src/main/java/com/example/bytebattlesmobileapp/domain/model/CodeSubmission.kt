package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class CodeSubmission(
    val id: UUID,
    val taskId: UUID,
    val userId: String,
    val code: String,
    val languageId: UUID,
    val status: SubmissionStatus,
    val testResults: List<TestResult>?,
    val executionTime: Long?,
    val submittedAt: String
)