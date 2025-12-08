package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.TaskApiService
import com.example.bytebattlesmobileapp.data.network.dto.task.LanguageDto
import com.example.bytebattlesmobileapp.data.network.dto.task.LibraryDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskLanguageDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TestCaseDto
import com.example.bytebattlesmobileapp.domain.model.Language
import com.example.bytebattlesmobileapp.domain.model.Library
import com.example.bytebattlesmobileapp.domain.model.Task
import com.example.bytebattlesmobileapp.domain.model.TaskLanguage
import com.example.bytebattlesmobileapp.domain.model.TestCase
import com.example.bytebattlesmobileapp.domain.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val taskApi: TaskApiService
) : TaskRepository {
    override suspend fun getTaskById(taskId: UUID): Task {
        val response = taskApi.getTaskById(taskId)
        return response.toDomain()
    }

    override suspend fun getTasksWithPagination(
        page: Int,
        pageSize: Int,
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<Task> {
        val response = taskApi.getTasksWithPagination(
            page,
            pageSize, searchTerm, difficulty, languageId
        )

        android.util.Log.d("TaskImpl", response.size.toString())
        return response.map { it.toDomain() }
    }

    override suspend fun getTasks(
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<Task> {
        val response = taskApi.getTasks(searchTerm, difficulty, languageId)
        return response.map { it.toDomain() }
    }

    override suspend fun getLanguageById(languageId: UUID): Language {
        val response = taskApi.getLanguageById(languageId)
        return response.toDomain()
    }

    override suspend fun getLanguages(
        searchTerm: String?,
        difficulty: String?,
        languageId: String?
    ): List<com.example.bytebattlesmobileapp.domain.model.Language> {
        val response = taskApi.getLanguages(searchTerm, difficulty, languageId)
        return response.map { it.toDomain() }
    }

    private fun TaskDto.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            difficulty = difficulty,
            author = author,
            functionName = functionName,
            patternMain = patternMain,
            patternFunction = patternFunction,
            parameters = parameters,
            returnType = returnType,
            createdAt = createdAt,
            updatedAt = updatedAt,
            totalAttempts = totalAttempts,
            successRate = successRate,
            successfulAttempts = successfulAttempts,
            averageExecutionTime = averageExecutionTime,
            language = language?.toDomain(),
            taskLanguages = taskLanguages.map { it.toDomain() },
            libraries = libraries.map { it.toDomain() },
            testCases = testCaseDtos.map { it.toDomain() }
        )
    }

    private fun TestCaseDto.toDomain(): TestCase {
        return TestCase(
            id, input, output, isExample
        )
    }

    private fun LanguageDto.toDomain(): Language {
        return Language(
            id = id,
            title = title,
            shortTitle = shortTitle,
            fileExtension = fileExtension,
            compilerCommand = compilerCommand,
            executionCommand = executionCommand,
            supportsCompilation = supportsCompilation,
            patternMain = patternMain,
            patternFunction = patternFunction,
            libraries = libraries.map { it?.toDomain() } ?: emptyList()
        )
    }

    private fun TaskLanguageDto.toDomain(): TaskLanguage {
        return TaskLanguage(
            languageId = languageId,
            languageTitle = languageTitle,
            languageShortTitle = languageShortTitle,
            c = c, // Поле 'c' из JSON, можно переименовать в codeTemplate в модели
            compilerCommand = compilerCommand,
            executionCommand = executionCommand,
            supportsCompilation = supportsCompilation
        )
    }

    private fun LibraryDto.toDomain(): Library {
        return Library(
            id = id,
            name = name,
            description = description,
            version = version,
            languageId = languageId
        )
    }

}