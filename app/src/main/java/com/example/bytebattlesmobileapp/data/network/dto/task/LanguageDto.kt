package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Serializable

@Serializable
data class LanguageDto(
    val id: String,
    val title: String,
    val shortTitle: String,
    val fileExtension: String,
    val compilerCommand: String,
    val executionCommand: String,
    val supportsCompilation: Boolean,
    val patternMain: String,
    val patternFunction: String,
    val libraries: List<LibraryDto>
)