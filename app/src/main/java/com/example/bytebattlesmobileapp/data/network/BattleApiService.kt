package com.example.bytebattlesmobileapp.data.network

import kotlinx.coroutines.flow.Flow

interface BattleApiService {
    suspend fun connect(): Result<Unit>
    suspend fun disconnect()
    suspend fun sendMessage(message: OutgoingBattleMessage)
    fun getMessages(): Flow<IncomingBattleMessage>
    fun getConnectionState(): Flow<BattleConnectionState>
}

data class BattleConnectionState(
    val isConnected: Boolean = false,
    val error: String? = null
)


sealed class IncomingBattleMessage {
    data class Connected(val playerId: String, val message: String) : IncomingBattleMessage()
    data class RoomCreated(
        val roomId: String,
        val roomName: String,
        val difficulty: String,
        val message: String
    ) : IncomingBattleMessage()

    data class JoinedRoom(
        val roomId: String,
        val roomName: String,
        val participants: Int,
        val status: String,
        val canStart: Boolean,
        val message: String
    ) : IncomingBattleMessage()

    data class PlayerJoined(
        val playerId: String,
        val participants: Int,
        val roomStatus: String
    ) : IncomingBattleMessage()

    data class PlayerLeft(
        val playerId: String,
        val participants: Int
    ) : IncomingBattleMessage()

    data class RoomStatus(
        val roomId: String,
        val status: String,
        val participantCount: Int,
        val readyCount: Int,
        val canStart: Boolean,
        val isActive: Boolean
    ) : IncomingBattleMessage()

    data class GameCanStart(
        val message: String,
        val countdown: Int
    ) : IncomingBattleMessage()

    data class PlayerReadyChanged(
        val playerId: String,
        val isReady: Boolean,
        val readyCount: Int,
        val totalPlayers: Int
    ) : IncomingBattleMessage()

    data class PlayerReadySet(
        val isReady: Boolean,
        val message: String
    ) : IncomingBattleMessage()

    data class GameStarted(
        val message: String,
        val startTime: String,
        val duration: Int,
        val taskId: String? = null, // Добавляем taskId
        val taskTitle: String? = null // Или taskTitle
    ) : IncomingBattleMessage()

    data class ReadinessTimeout(
        val message: String,
        val readyCount: Int,
        val totalPlayers: Int
    ) : IncomingBattleMessage()

    data class CodeSubmitted(
        val taskTitle: String
    ) : IncomingBattleMessage()

    data class CodeSubmittedByPlayer(
        val playerId: String,
        val taskTitle: String
    ) : IncomingBattleMessage()

    data class CodeResult(
        val result: CodeResultData,
        val testResults: List<TestResult>?
    ) : IncomingBattleMessage()

    data class BattleWon(
        val winnerId: String,
        val taskTitle: String,
        val message: String,
        val timestamp: String
    ) : IncomingBattleMessage()

    data class BattleLost(
        val winnerId: String,
        val taskTitle: String,
        val message: String,
        val timestamp: String
    ) : IncomingBattleMessage()

    data class BattleFinished(
        val winnerId: String,
        val taskTitle: String,
        val message: String,
        val timestamp: String
    ) : IncomingBattleMessage()

    data class LeftRoom(
        val roomId: String
    ) : IncomingBattleMessage()

    data class PlayerDisconnected(
        val playerId: String,
        val participants: Int?
    ) : IncomingBattleMessage()

    data class Error(val message: String) : IncomingBattleMessage()
    data class Unknown(val json: String) : IncomingBattleMessage()

    object Disconnected : IncomingBattleMessage()
}

// Исходящие сообщения для отправки на сервер
sealed class OutgoingBattleMessage {
    data class CreateRoom(
        val roomName: String,
        val languageId: String,
        val difficulty: String
    ) : OutgoingBattleMessage()

    data class JoinRoom(
        val roomId: String
    ) : OutgoingBattleMessage()

    data class PlayerReady(
        val roomId: String,
        val isReady: Boolean
    ) : OutgoingBattleMessage()

    data class SubmitCode(
        val roomId: String,
        val code: String
    ) : OutgoingBattleMessage()

    data class LeaveRoom(
        val roomId: String
    ) : OutgoingBattleMessage()
}

data class CodeResultData(
    val status: String,
    val passedTests: Int,
    val totalTests: Int,
    val executionTime: Int
)

data class TestResult(
    val status: String,
    val input: String,
    val expectedOutput: String,
    val actualOutput: String,
    val executionTime: Int
)