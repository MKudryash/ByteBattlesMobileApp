package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.UserApiService
import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UserLeaderDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserProfileDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserSettingsDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserStatsDto
import com.example.bytebattlesmobileapp.domain.model.UserLeader
import com.example.bytebattlesmobileapp.domain.model.UserSettings
import com.example.bytebattlesmobileapp.domain.model.UserProfile
import com.example.bytebattlesmobileapp.domain.model.UserStats
import com.example.bytebattlesmobileapp.domain.repository.UserRepository
import kotlin.Int

class UserRepositoryImpl(
    private val userApi: UserApiService
) : UserRepository {


    override suspend fun getProfile(): UserProfile {
        val response = userApi.getProfile()
        return response.toDomain()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserProfile {
        val response = userApi.updateProfile(request)
        return response.toDomain()
    }

    override suspend fun getUserStats(userId: String): UserStats {
        val response = userApi.getUserStats(userId)
        return response.toDomain()
    }

    override suspend fun getLeaderBord(): List<UserLeader> {
        val response = userApi.getLeaderBord()
        return response.map { it.toDomain() }
    }

    private fun UserLeaderDto.toDomain(): UserLeader {
        return UserLeader(
            userId,
            userName,
            avatarUrl,
            country,
            position,
            totalExperience,
            battlesWon,
            problemsSolved,
            level
        )
    }

    private fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            id = id,
            userId = userId,
            userName = userName,
            avatarUrl = avatarUrl,
            bio, gitHubUrl, linkedInUrl, level,
            settings.toDomain(), stats?.toDomain(), createdAt, isPublic, email = email, country
        )
    }

    private fun UserStatsDto.toDomain(): UserStats {
        return UserStats(
            totalProblemsSolved,
            totalBattles,
            wins,
            losses,
            draws,
            currentStreak,
            maxStreak,
            totalExperience,
            winRate,
            experienceToNextLevel,
            easyProblemsSolved,
            mediumProblemsSolved,
            hardProblemsSolved,
            totalSubmissions,
            successfulSubmissions,
            totalExecutionTime,
            solvedTaskIds,
            successRate,
            averageExecutionTime
        )
    }

    private fun UserSettingsDto.toDomain(): UserSettings {
        return UserSettings(
            emailNotifications, battleInvitations, achievementNotifications,
            theme, codeEditorTheme, preferredLanguage
        )
    }


}