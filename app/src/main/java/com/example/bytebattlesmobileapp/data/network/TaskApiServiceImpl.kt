package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionResponse
import com.example.bytebattlesmobileapp.data.network.dto.task.SubmitSolutionRequest
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import java.util.UUID

class TaskApiServiceImpl(private val client: HttpClient) : TaskApiService {
    override suspend fun getTasks(page: Int, pageSize: Int): TaskListResponse {
        return client.get("tasks") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }

    override suspend fun getTaskById(taskId: UUID): TaskDto {
        return client.get("tasks/$taskId").body()
    }

    override suspend fun getTasksByDifficulty(difficulty: String): List<TaskDto> {
        return client.get("tasks/difficulty/$difficulty").body()
    }

    override suspend fun getTasksByLanguage(languageId: UUID): List<TaskDto> {
        return client.get("tasks/language/$languageId").body()
    }

    override suspend fun submitSolution(request: SubmitSolutionRequest): CodeSubmissionResponse {
        return client.post("tasks/submit") {
            setBody(request)
        }.body()
    }

    override suspend fun getSubmissionStatus(submissionId: UUID): CodeSubmissionResponse {
        return client.get("submissions/$submissionId").body()
    }
}