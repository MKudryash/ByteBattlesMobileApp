package com.example.bytebattlesmobileapp.domain.model

import kotlinx.serialization.Serializable


data class Language(
    val id: String,
    val title: String,
    val shortTitle: String,
    val fileExtension: String,
    val compilerCommand: String,
    val executionCommand: String,
    val supportsCompilation: Boolean,
    val patternMain: String,
    val patternFunction: String,
    val libraries: List<Library?>,
)