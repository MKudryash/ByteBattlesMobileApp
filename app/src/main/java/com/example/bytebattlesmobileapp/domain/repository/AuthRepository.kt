// domain/repository/
package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.domain.model.*

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(firstName: String, lastName:String, email: String, password: String, role: String = "student"): User
    suspend fun refreshToken(): String
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}

data class UserStats(
    val userId: String,
    val rating: Int,
    val rank: Int,
    val battlesTotal: Int,
    val battlesWon: Int,
    val winRate: Double,
    val averageCompletionTime: Long?,
    val favoriteLanguage: String?,
    val tasksSolved: Int
)