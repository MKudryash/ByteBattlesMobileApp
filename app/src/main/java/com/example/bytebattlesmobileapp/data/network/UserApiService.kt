package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.AchievementDto
import com.example.bytebattlesmobileapp.data.network.dto.ActivitiesDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.data.network.dto.user.UserLeaderDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserProfileDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UserStatsDto

interface UserApiService {
    suspend fun getProfile(): UserProfileDto
    suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto
    suspend fun getUserStats(userId: String): UserStatsDto
    suspend fun getLeaderBord():List<UserLeaderDto>

    suspend fun getRecentActivities():List<ActivitiesDto>
    suspend fun getAchievements():List<AchievementDto>
}