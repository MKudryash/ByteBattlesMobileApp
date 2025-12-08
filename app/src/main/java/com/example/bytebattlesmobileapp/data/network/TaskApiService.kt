package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionResponse
import com.example.bytebattlesmobileapp.data.network.dto.task.SubmitSolutionRequest
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskListResponse
import java.util.UUID

interface TaskApiService {
    suspend fun getTasks(page: Int = 1, pageSize: Int = 10): TaskListResponse
    suspend fun getTaskById(taskId: UUID): TaskDto
    suspend fun getTasksByDifficulty(difficulty: String): List<TaskDto>
    suspend fun getTasksByLanguage(languageId: UUID): List<TaskDto>
    suspend fun submitSolution(request: SubmitSolutionRequest): CodeSubmissionResponse
    suspend fun getSubmissionStatus(submissionId: UUID): CodeSubmissionResponse
}