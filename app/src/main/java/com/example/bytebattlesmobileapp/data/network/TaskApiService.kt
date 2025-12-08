package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionResponse
import com.example.bytebattlesmobileapp.data.network.dto.task.LanguageDto
import com.example.bytebattlesmobileapp.data.network.dto.task.TaskDto
import java.util.UUID

interface TaskApiService {
    suspend fun getTaskById(taskId: UUID): TaskDto
    suspend fun getTasksWithPagination(page:Int,pageSize:Int,searchTerm:String?,
                                       difficulty: String?,languageId: String? ):List<TaskDto>
    suspend fun getTasks(searchTerm:String?,
                         difficulty: String?,languageId: String? ):List<TaskDto>

    suspend fun getLanguageById(languageId: UUID): LanguageDto

    suspend fun getLanguages(searchTerm:String?,
                             difficulty: String?,languageId: String? ):List<LanguageDto>


}