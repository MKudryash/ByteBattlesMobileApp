package com.example.bytebattlesmobileapp.data.network.dto.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskLanguageDto(
    val languageId: String,
    val languageTitle: String,
    val languageShortTitle: String,
    val c: String, // Замените на более осмысленное имя при необходимости
    val compilerCommand: String,
    val executionCommand: String,
    val supportsCompilation: Boolean
)