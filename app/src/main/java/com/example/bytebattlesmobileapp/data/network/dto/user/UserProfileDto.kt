package com.example.bytebattlesmobileapp.data.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserStatsDto(
    @SerialName("totalProblemsSolved")
    val totalProblemsSolved: Int,
    @SerialName("totalBattles")
    val totalBattles: Int,
    @SerialName("wins")
    val wins: Int,

    @SerialName("losses")
    val losses: Int,

    @SerialName("draws")
    val draws: Int,

    @SerialName("currentStreak")
    val currentStreak: Int,

    @SerialName("maxStreak")
    val maxStreak: Int,

    @SerialName("totalExperience")
    val totalExperience: Int,

    @SerialName("winRate")
    val winRate: Double,

    @SerialName("experienceToNextLevel")
    val experienceToNextLevel: Int,

    @SerialName("easyProblemsSolved")
    val easyProblemsSolved: Int,

    @SerialName("mediumProblemsSolved")
    val mediumProblemsSolved: Int,

    @SerialName("hardProblemsSolved")
    val hardProblemsSolved: Int,

    @SerialName("totalSubmissions")
    val totalSubmissions: Int,

    @SerialName("successfulSubmissions")
    val successfulSubmissions: Int,

    @SerialName("totalExecutionTime")
    val totalExecutionTime: String,

    @SerialName("solvedTaskIds")
    val solvedTaskIds: List<String>,

    @SerialName("successRate")
    val successRate: Double,

    @SerialName("averageExecutionTime")
    val averageExecutionTime: String

)
@Serializable
data class UserProfileDto(
    val id: String,
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val bio: String?,
    val gitHubUrl: String?,
    val linkedInUrl: String?,
    val level: String?,
    val stats: UserStatsDto?,
    val settings: UserSettingsDto,
    val createdAt: String,
    val isPublic: Boolean,
    val email:String,
    val country: String?,
)
@Serializable
data class UserLeaderDto(
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

@Serializable
data class UserSettingsDto(
    val emailNotifications: Boolean,
    val battleInvitations: Boolean,
    val achievementNotifications: Boolean,
    val theme: String,
    val codeEditorTheme: String,
    val preferredLanguage: String
)