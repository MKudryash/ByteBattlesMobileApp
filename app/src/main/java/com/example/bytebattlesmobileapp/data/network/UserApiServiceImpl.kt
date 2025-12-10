package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.AchievementDto
import com.example.bytebattlesmobileapp.data.network.dto.ActivitiesDto
import com.example.bytebattlesmobileapp.data.network.dto.battle.*
import com.example.bytebattlesmobileapp.data.network.dto.user.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserApiServiceImpl(private val client: HttpClient) : UserApiService {
    override suspend fun getProfile(): UserProfileDto {
        return client.get("user-profiles/me").body()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto {
        return client.put("user-profiles/me") {
            setBody(request)
        }.body()
    }

    override suspend fun getUserStats(userId: String): UserStatsDto {
        return client.get("user-profiles/me/stats").body()
    }

    override suspend fun getLeaderBord(): List<UserLeaderDto> {
        return client.get("user-profiles/leaderboard").body()

    }

    override suspend fun getRecentActivities(): List<ActivitiesDto> {
        return client.get("user-profiles/me/recent-activities").body()
    }

    override suspend fun getAchievements(): List<AchievementDto> {
      return client.get("user-profiles/achievements").body()
    }

}