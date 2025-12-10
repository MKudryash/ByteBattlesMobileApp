// domain/repository/
package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.data.network.dto.ChangePasswordDto
import com.example.bytebattlesmobileapp.domain.model.*

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(firstName: String, lastName:String, email: String, password: String, role: String = "student"): User
    suspend fun refreshToken(): String
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    suspend fun passwordChange(oldPassword: String, newPassword: String)
}
