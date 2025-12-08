package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val description: String,
    val difficulty: TaskDifficulty,
    val languageId: UUID,
    val timeLimit: Int,
    val memoryLimit: Int,
    val examples: List<TaskExample>,
    val testCases: List<TestCase>,
    val tags: List<String>
)