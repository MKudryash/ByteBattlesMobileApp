package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.TaskApiService
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskExampleDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TestCaseDto
import com.example.bytebattlesmobileapp.domain.model.Task
import com.example.bytebattlesmobileapp.domain.model.TaskDifficulty
import com.example.bytebattlesmobileapp.domain.model.TaskExample
import com.example.bytebattlesmobileapp.domain.model.TestCase
import com.example.bytebattlesmobileapp.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskApi: TaskApiService
) : TaskRepository {

    override suspend fun getTasks(page: Int, pageSize: Int): List<Task> {
        val response = taskApi.getTasks(page, pageSize)
        return response.tasks.map { it.toDomain() }
    }

    override suspend fun getTaskById(taskId: UUID): Task {
        val response = taskApi.getTaskById(taskId)
        return response.toDomain()
    }

    override suspend fun getTasksByDifficulty(difficulty: TaskDifficulty): List<Task> {
        val response = taskApi.getTasksByDifficulty(difficulty.name)
        return response.map { it.toDomain() }
    }

    override suspend fun getTasksByLanguage(languageId: UUID): List<Task> {
        val response = taskApi.getTasksByLanguage(languageId)
        return response.map { it.toDomain() }
    }

  /*  override suspend fun submitSolution(taskId: UUID, code: String, languageId: UUID): CodeSubmission {
        val request = SubmitSolutionRequest(taskId, code, languageId)
        val response = taskApi.submitSolution(request)
        return response.toDomain()
    }

    override suspend fun getSubmissionStatus(submissionId: UUID): CodeSubmission {
        val response = taskApi.getSubmissionStatus(submissionId)
        return response.toDomain()
    }*/


    private fun TaskDto.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            difficulty = TaskDifficulty.valueOf(difficulty.uppercase()),
            languageId = languageId,
            timeLimit = timeLimit,
            memoryLimit = memoryLimit,
            examples = examples.map { it.toDomain() },
            testCases = testCases.map { it.toDomain() },
            tags = tags
        )
    }

    private fun TaskExampleDto.toDomain(): TaskExample {
        return TaskExample(
            input = input,
            output = output,
            explanation = explanation
        )
    }

    private fun TestCaseDto.toDomain(): TestCase {
        return TestCase(
            id = id,
            input = input,
            expectedOutput = expectedOutput,
            isPublic = isPublic
        )
    }
}