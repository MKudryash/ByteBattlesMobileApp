package com.example.bytebattlesmobileapp.data.network.dto.solution

import kotlinx.serialization.Serializable


@Serializable
data class SubmitSolutionDto(
    val taskId: String,
    val languageId: String,
    val code: String
)
@Serializable
data class SolutionDto(
    val id: String,
    val taskId: String,
    val userId: String,
    val languageId: String,
    val status: String?,
    val submittedAt: String?,
    val completedAt: String?,
    val executionTime: String?,
    val memoryUsed: Int?,
    val passedTests: Int?,
    val totalTests: Int?,
    val successRate: Int?,
    val testResults: List<TestResultDto>,
    val attempts: List<SolutionAttemptDto>,
)

@Serializable
data class SolutionAttemptDto(
    val id: String,
    val code: String?,
    val attemptedAt: String?,
    val status: String?,
    val executionTime: String?,
    val memoryUsed: Int?,
)
@Serializable
data class TestResultDto(
    val id: String,
    val status: String?,
    val input: String?,
    val expectedOutput: String?,
    val actualOutput: String?,
    val executionTime: String?,
    val errorMessage: String?,
    val memoryUsed: Int?,
)
