package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TaskDto(
    @Contextual
    val id: UUID,
    val title: String,
    val description: String,
    val difficulty: String,
    @Contextual
    val languageId: UUID,
    val timeLimit: Int, // seconds
    val memoryLimit: Int, // MB
    val examples: List<TaskExampleDto>,
    val testCases: List<TestCaseDto>,
    val tags: List<String>,
    val authorId: String,
    val createdAt: String
)

