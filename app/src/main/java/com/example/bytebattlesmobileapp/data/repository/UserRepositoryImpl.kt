package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.UserApiService
import com.example.bytebattlesmobileapp.data.network.dto.battle.ChangePasswordRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UserProfileDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserStatsDto
import com.example.bytebattlesmobileapp.domain.model.User
import com.example.bytebattlesmobileapp.domain.repository.UserRepository
import com.example.bytebattlesmobileapp.domain.repository.UserStats

class UserRepositoryImpl(
    private val userApi: UserApiService
) : UserRepository {

    override suspend fun getUserProfile(): User {
        val response = userApi.getProfile()
        return response.toDomain()
    }

    override suspend fun updateProfile(username: String?, email: String?): User {
        val request = UpdateProfileRequest(username, email)
        val response = userApi.updateProfile(request)
        return response.toDomain()
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        val request = ChangePasswordRequest(oldPassword, newPassword)
        userApi.changePassword(request)
    }

    override suspend fun getUserStats(userId: String): UserStats {
        val response = userApi.getUserStats(userId)
        return response.toDomain()
    }

    override suspend fun getUserRankings(page: Int, pageSize: Int): List<UserStats> {
        val response = userApi.getUserRankings(page, pageSize)
        return response.map { it.toDomain() }
    }

    private fun UserProfileDto.toDomain(): User {
        return User(
            id = id,
            username = username,
            email = email,
            rating = rating,
            battlesWon = battlesWon,
            battlesLost = battlesLost,
            tasksSolved = tasksSolved
        )
    }

    private fun UserStatsDto.toDomain(): UserStats {
        return UserStats(
            userId = userId,
            rating = rating,
            rank = rank,
            battlesTotal = battlesTotal,
            battlesWon = battlesWon,
            winRate = winRate,
            averageCompletionTime = averageCompletionTime,
            favoriteLanguage = favoriteLanguage,
            tasksSolved = tasksSolved
        )
    }
}