package com.example.bytebattlesmobileapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
data class UserProfile(
    val id: String,
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val bio: String?,
    val gitHubUrl: String?,
    val linkedInUrl: String?,
    val level: String?,
    val settings: UserSettings,
    val stats: UserStats?,
    val createdAt: String,
    val isPublic: Boolean,
    val email: String,
    val country: String?,

)


data class UserStats(
    val totalProblemsSolved: Int,
    val totalBattles: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val currentStreak: Int,
    val maxStreak: Int,
    val totalExperience: Int,
    val winRate: Double,
    val experienceToNextLevel: Int,
    val easyProblemsSolved: Int,
    val mediumProblemsSolved: Int,
    val hardProblemsSolved: Int,
    val totalSubmissions: Int,
    val successfulSubmissions: Int,
    val totalExecutionTime: String,
    val solvedTaskIds: List<String>,
    val successRate: Double,
    val averageExecutionTime: String
)

data class UserLeader(
    val userId: String,
    val username: String,
    val avatarUrl: String,
    val country: String,
    val position: Int,
    val totalExperience: Int,
    val battlesWon: Int,
    val problemsSolved: Int,
    val level: String
)

data class UserSettings(
    val emailNotifications: Boolean,
    val battleInvitations: Boolean,
    val achievementNotifications: Boolean,
    val theme: String,
    val codeEditorTheme: String,
    val preferredLanguage: String
)
