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
    val id: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val gitHubUrl: String? = null,
    val linkedInUrl: String? = null,
    val level: String? = null,
    val stats: UserStatsDto? = null,
    val settings: UserSettingsDto? = null,
    val createdAt: String? = null,
    val isPublic: Boolean? = null,
    val email: String? = null,
    val country: String? = null,
)

@Serializable
data class UserLeaderDto(
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val country: String?,
    val position: Int?,
    val totalExperience: Int?,
    val battlesWon: Int?,
    val problemsSolved: Int?,
    val level: String?
)

@Serializable
data class UserSettingsDto(
    val emailNotifications: Boolean?,
    val battleInvitations: Boolean?,
    val achievementNotifications: Boolean?,
    val theme: String?,
    val codeEditorTheme: String?,
    val preferredLanguage: String??
)