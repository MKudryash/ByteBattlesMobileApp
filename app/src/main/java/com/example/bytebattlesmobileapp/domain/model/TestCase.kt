package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class TestCase(
    val id: UUID,
    val input: String,
    val expectedOutput: String,
    val isPublic: Boolean
)