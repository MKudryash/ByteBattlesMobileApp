package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class TestResult(
    val testId: UUID,
    val status: TestStatus,
    val input: String,
    val expectedOutput: String,
    val actualOutput: String?,
    val executionTime: Long
)