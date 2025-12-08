package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.domain.model.User

interface UserRepository {
    suspend fun getUserProfile(): User
    suspend fun updateProfile(username: String? = null, email: String? = null): User
    suspend fun changePassword(oldPassword: String, newPassword: String)
    suspend fun getUserStats(userId: String): UserStats
    suspend fun getUserRankings(page: Int = 1, pageSize: Int = 20): List<UserStats>
}