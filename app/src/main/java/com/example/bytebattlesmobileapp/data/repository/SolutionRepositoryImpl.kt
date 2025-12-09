package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.SolutionApiService
import com.example.bytebattlesmobileapp.data.network.dto.solution.SolutionAttemptDto
import com.example.bytebattlesmobileapp.data.network.dto.solution.SolutionDto
import com.example.bytebattlesmobileapp.data.network.dto.solution.SubmitSolutionDto
import com.example.bytebattlesmobileapp.data.network.dto.solution.TestResultDto
import com.example.bytebattlesmobileapp.domain.model.Solution
import com.example.bytebattlesmobileapp.domain.model.SolutionAttempt
import com.example.bytebattlesmobileapp.domain.model.TestResult
import com.example.bytebattlesmobileapp.domain.repository.SolutionRepository

class SolutionRepositoryImpl(
    private val solutionApi: SolutionApiService
) : SolutionRepository {
    override suspend fun submitSolution(
        code: String,
        languageId: String,
        taskId: String
    ): Solution {
        val response = solutionApi.submitSolution(SubmitSolutionDto(taskId, languageId, code))
        return response.toDomain()
    }

    private fun SolutionDto.toDomain(): Solution {
        return Solution(
            id,
            taskId,
            userId,
            languageId,
            status,
            submittedAt,
            completedAt,
            executionTime,
            memoryUsed,
            passedTests,
            totalTests,
            successRate,
            testResults.map { it.toDomain() },
            attempts.map {
                it.toDomain()
            }
        )
    }

    private fun SolutionAttemptDto.toDomain(): SolutionAttempt {
        return SolutionAttempt(
            id, code, attemptedAt, status, executionTime, memoryUsed
        )
    }

    private fun TestResultDto.toDomain(): TestResult {
        return TestResult(
            id, status, input, expectedOutput, actualOutput, executionTime, errorMessage, memoryUsed
        )
    }
}