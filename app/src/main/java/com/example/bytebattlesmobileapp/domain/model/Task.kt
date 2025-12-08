package com.example.bytebattlesmobileapp.domain.model


data class Task(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val author: String,
    val functionName: String,
    val patternMain: String,
    val patternFunction: String,
    val parameters: String,
    val returnType: String,
    val createdAt: String,
    val updatedAt: String,
    val totalAttempts: Int,
    val successfulAttempts: Int,
    val successRate: Double,
    val averageExecutionTime: Double,
    val language: Language?,
    val taskLanguages: List<TaskLanguage>,
    val libraries: List<Library>,
    val testCases: List<TestCase>
)

