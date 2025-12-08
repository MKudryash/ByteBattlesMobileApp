package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.battle.ChangePasswordRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UserProfileDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserStatsDto

interface UserApiService {
    suspend fun getProfile(): UserProfileDto
    suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto
    suspend fun changePassword(request: ChangePasswordRequest)
    suspend fun getUserStats(userId: String): UserStatsDto
    suspend fun getUserRankings(page: Int = 1, pageSize: Int = 20): List<UserStatsDto>
}