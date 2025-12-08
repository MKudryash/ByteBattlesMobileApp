package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Serializable

@Serializable
data class LibraryDto(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val languageId: String
)