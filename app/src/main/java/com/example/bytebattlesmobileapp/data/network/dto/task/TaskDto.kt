package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
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
    val language: LanguageDto,
    val taskLanguages: List<TaskLanguageDto>,
    val libraries: List<LibraryDto>,
    @SerialName("testCases")
    val testCaseDtos: List<TestCaseDto>
) {

}

