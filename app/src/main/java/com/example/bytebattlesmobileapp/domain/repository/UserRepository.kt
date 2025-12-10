package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.data.network.dto.AchievementDto
import com.example.bytebattlesmobileapp.data.network.dto.ActivitiesDto
import com.example.bytebattlesmobileapp.data.network.dto.user.UpdateProfileRequest
import com.example.bytebattlesmobileapp.domain.model.Achievement
import com.example.bytebattlesmobileapp.domain.model.Activities
import com.example.bytebattlesmobileapp.domain.model.User
import com.example.bytebattlesmobileapp.domain.model.UserLeader
import com.example.bytebattlesmobileapp.domain.model.UserProfile
import com.example.bytebattlesmobileapp.domain.model.UserStats

interface UserRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(request: UpdateProfileRequest): UserProfile
    suspend fun getUserStats(userId: String): UserStats
    suspend fun getLeaderBord():List<UserLeader>
    suspend fun getRecentActivities():List<Activities>
    suspend fun getAchievements():List<Achievement>
}