package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.domain.model.CodeSubmission
import com.example.bytebattlesmobileapp.domain.model.Task
import com.example.bytebattlesmobileapp.domain.model.TaskDifficulty
import java.util.UUID

interface TaskRepository {
    suspend fun getTasks(page: Int = 1, pageSize: Int = 10): List<Task>
    suspend fun getTaskById(taskId: UUID): Task
    suspend fun getTasksByDifficulty(difficulty: TaskDifficulty): List<Task>
    suspend fun getTasksByLanguage(languageId: UUID): List<Task>
  /*  suspend fun submitSolution(taskId: UUID, code: String, languageId: UUID): CodeSubmission*/
   /* suspend fun getSubmissionStatus(submissionId: UUID): CodeSubmission*/
}