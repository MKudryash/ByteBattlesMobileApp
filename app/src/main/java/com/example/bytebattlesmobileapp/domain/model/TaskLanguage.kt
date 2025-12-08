package com.example.bytebattlesmobileapp.domain.model

data class TaskLanguage(
    val languageId: String,
    val languageTitle: String,
    val languageShortTitle: String,
    val c: String, // Замените на более осмысленное имя при необходимости
    val compilerCommand: String,
    val executionCommand: String,
    val supportsCompilation: Boolean
)