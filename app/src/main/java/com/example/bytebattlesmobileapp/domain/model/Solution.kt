package com.example.bytebattlesmobileapp.domain.model

import kotlinx.serialization.Serializable



data class Solution(
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
    val testResult: List<TestResult>,
    val attempts: List<SolutionAttempt>,
)


data class SolutionAttempt(
    val id: String,
    val code: String?,
    val attemptedAt: String?,
    val status: String?,
    val executionTime: String?,
    val memoryUsed: Int?,
)
data class TestResult(
    val id: String,
    val status: String?,
    val input: String?,
    val expectedOutput: String?,
    val actualOutput: String?,
    val executionTime: String?,
    val errorMessage: String?,
    val memoryUsed: Int?,
)

