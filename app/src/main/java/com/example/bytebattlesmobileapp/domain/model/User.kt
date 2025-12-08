package com.example.bytebattlesmobileapp.domain.model


data class User(
    val id: String,
    val username: String,
    val email: String,
    val rating: Int,
    val battlesWon: Int,
    val battlesLost: Int,
    val tasksSolved: Int
)

data class TaskExample(
    val input: String,
    val output: String,
    val explanation: String?
)

