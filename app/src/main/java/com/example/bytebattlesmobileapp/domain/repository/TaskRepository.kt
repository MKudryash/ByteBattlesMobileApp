package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.data.network.dto.task.LanguageDto
import com.example.bytebattlesmobileapp.domain.model.Language
import com.example.bytebattlesmobileapp.domain.model.Task
import java.util.UUID

interface TaskRepository {

    suspend fun getTaskById(taskId: UUID): Task
    suspend fun getTasksWithPagination(page:Int=1,pageSize:Int=10,searchTerm:String?,
                                       difficulty: String?,languageId: String? ):List<Task>
    suspend fun getTasks(searchTerm:String?,
                         difficulty: String?,languageId: String? ):List<Task>
    suspend fun getLanguageById(languageId: UUID): Language

    suspend fun getLanguages(searchTerm:String?,
                             difficulty: String?,languageId: String? ):List<Language>
}