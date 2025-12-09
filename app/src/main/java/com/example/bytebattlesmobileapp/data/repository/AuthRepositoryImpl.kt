package com.example.bytebattlesmobileapp.data.repository


import android.util.Log
import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import com.example.bytebattlesmobileapp.data.network.*
import com.example.bytebattlesmobileapp.data.network.dto.auth.AuthResponse
import com.example.bytebattlesmobileapp.data.network.dto.auth.RefreshTokenRequest
import com.example.bytebattlesmobileapp.data.network.dto.auth.RegisterRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.*
import com.example.bytebattlesmobileapp.di.AuthException
import com.example.bytebattlesmobileapp.domain.model.*
import com.example.bytebattlesmobileapp.domain.repository.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepositoryImpl(
    private val authApi: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        try {
            val response = authApi.login(LoginRequest(email, password))
            tokenManager.saveToken(response.accessToken, response.refreshToken)
            // Предполагается, что response содержит user информацию
            return response.toDomain() // или другой способ получения User
        } catch (e: UnknownHostException) {
            throw AuthException("Нет подключения к интернету")
        } catch (e: ConnectException) {
            throw AuthException("Не удалось подключиться к серверу")
        } catch (e: SocketTimeoutException) {
            throw AuthException("Таймаут подключения")
        } catch (e: Exception) {
            throw AuthException("Ошибка авторизации: ${e.message}")
        }
    }

    override suspend fun register(firstName: String, lastName:String, email: String, password: String, role: String): User {
        val response = authApi.register(RegisterRequest(firstName,lastName,email,password,role))
        tokenManager.saveToken(response.accessToken, response.refreshToken)
        return response.toDomain()
    }

    override suspend fun refreshToken(): String {
        val (token, refreshToken) = tokenManager.getTokens()
            ?: throw IllegalStateException("No tokens available")

        val response = authApi.refreshToken(RefreshTokenRequest(token, refreshToken))
        tokenManager.saveToken(response.accessToken, response.refreshToken)
        return response.accessToken
    }

    override suspend fun logout() {
        try {
            authApi.logout()
        } catch (e: Exception) {
            // Log logout error but continue
        } finally {
            tokenManager.clearTokens()
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getTokens() != null
    }

    override fun getCurrentUserId(): String? {
        return tokenManager.getCurrentUserId()
    }

    private fun AuthResponse.toDomain(): User {
        return User(
            id = user.id.toString(),
            username = user.firstName,
            email = user.email,
            rating = 1000, // Default rating
            battlesWon = 0,
            battlesLost = 0,
            tasksSolved = 0
        )
    }
}

/*class BattleRepositoryImpl(
    private val battleApi: BattleApiService,
    private val webSocketManager: BattleWebSocketManager
) : BattleRepository {

    override suspend fun createBattleRoom(
        name: String,
        languageId: UUID,
        difficulty: BattleDifficulty
    ): BattleRoom {
        val request = BattleRoomRequest(
            roomName = name,
            languageId = languageId,
            difficulty = difficulty.name
        )
        val response = battleApi.createRoom(request)
        return response.toDomain()
    }

    override suspend fun joinBattleRoom(roomId: UUID): BattleRoom {
        val request = JoinRoomRequest(roomId)
        val response = battleApi.joinRoom(request)
        return response.toDomain()
    }

    override suspend fun getBattleRoomStatus(roomId: UUID): BattleRoom {
        val response = battleApi.getRoomStatus(roomId)
        return response.toDomain()
    }

    override suspend fun toggleReadyStatus(roomId: UUID, isReady: Boolean) {
        battleApi.toggleReady(roomId, isReady)
    }

    override suspend fun submitBattleCode(
        roomId: UUID,
        taskId: UUID,
        code: String,
        languageId: UUID
    ): CodeSubmission {
        val request = CodeSubmissionRequest(roomId, taskId, code, languageId)
        val response = battleApi.submitCode(request)
        return response.toDomain()
    }

    override suspend fun leaveBattleRoom(roomId: UUID) {
        battleApi.leaveRoom(roomId)
    }

    override suspend fun getBattleResult(battleId: UUID): BattleResult {
        val response = battleApi.getBattleResult(battleId)
        return response.toDomain()
    }

    override suspend fun getActiveBattleRooms(): List<BattleRoom> {
        val response = battleApi.getActiveBattles()
        return response.map { it.toDomain() }
    }

    override suspend fun connectToBattleWebSocket(roomId: UUID): Boolean {
        return webSocketManager.connect(roomId)
    }

    override suspend fun disconnectFromBattleWebSocket() {
        webSocketManager.disconnect()
    }

    private fun BattleRoomResponse.toDomain(): BattleRoom {
        return BattleRoom(
            id = roomId,
            name = roomName,
            languageId = languageId,
            difficulty = BattleDifficulty.valueOf(difficulty.uppercase()),
            hostId = hostId,
            participants = participants.map { it.toDomain() },
            status = BattleStatus.valueOf(status.uppercase()),
            createdAt = createdAt
        )
    }

    private fun BattleParticipantDto.toDomain(): BattleParticipant {
        return BattleParticipant(
            id = id,
            username = username,
            isReady = isReady,
            isHost = isHost,
            joinedAt = joinedAt
        )
    }

    private fun CodeSubmissionResponse.toDomain(): CodeSubmission {
        return CodeSubmission(
            id = submissionId,
            taskId = UUID.randomUUID(), // Should be from context
            userId = "", // Should be from context
            code = "", // Not available in response
            languageId = UUID.randomUUID(), // Should be from context
            status = SubmissionStatus.valueOf(status.uppercase()),
            testResults = testResults?.map { it.toDomain() },
            executionTime = executionTime,
            submittedAt = submittedAt
        )
    }

    private fun TestResultDto.toDomain(): TestResult {
        return TestResult(
            testId = testId,
            status = TestStatus.valueOf(status.uppercase()),
            input = input,
            expectedOutput = expectedOutput,
            actualOutput = actualOutput,
            executionTime = executionTime
        )
    }

    private fun BattleResultDto.toDomain(): BattleResult {
        return BattleResult(
            battleId = battleId,
            winnerId = winnerId,
            participants = participants.map { it.toDomain() },
            taskId = taskId,
            startedAt = startedAt,
            finishedAt = finishedAt,
            duration = duration
        )
    }

    private fun BattleParticipantResultDto.toDomain(): BattleParticipantResult {
        return BattleParticipantResult(
            userId = userId,
            username = username,
            score = score,
            submissionId = submissionId,
            completionTime = completionTime
        )
    }
}*/

