package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskListResponse(
    val tasks: List<TaskDto>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)