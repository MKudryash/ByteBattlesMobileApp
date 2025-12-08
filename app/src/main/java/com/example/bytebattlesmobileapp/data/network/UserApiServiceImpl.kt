package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.battle.*
import com.example.bytebattlesmobileapp.data.network.dto.user.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserApiServiceImpl(private val client: HttpClient) : UserApiService {
    override suspend fun getProfile(): UserProfileDto {
        return client.get("user/profile").body()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto {
        return client.put("user/profile") {
            setBody(request)
        }.body()
    }

    override suspend fun changePassword(request: ChangePasswordRequest) {
        client.put("user/password") {
            setBody(request)
        }
    }

    override suspend fun getUserStats(userId: String): UserStatsDto {
        return client.get("user/$userId/stats").body()
    }

    override suspend fun getUserRankings(page: Int, pageSize: Int): List<UserStatsDto> {
        return client.get("user/rankings") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }
}