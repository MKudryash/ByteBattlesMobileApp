package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class TestCase(
    val id: String,
    val input: String,
    val output: String,
    val isExample: Boolean
)