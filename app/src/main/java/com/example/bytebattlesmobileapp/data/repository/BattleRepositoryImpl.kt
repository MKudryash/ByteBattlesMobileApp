package com.example.bytebattlesmobileapp.data.repository

import com.example.bytebattlesmobileapp.data.network.*
import com.example.bytebattlesmobileapp.domain.repository.BattleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BattleRepositoryImpl @Inject constructor(
    private val battleApi: BattleApiService
) : BattleRepository {

    override suspend fun connect(token: String): Result<Unit> {
        return battleApi.connect()
    }

    override suspend fun disconnect() {
        battleApi.disconnect()
    }

    override suspend fun createRoom(roomName: String, languageId: String, difficulty: String) {
        battleApi.sendMessage(
            OutgoingBattleMessage.CreateRoom(
                roomName = roomName,
                languageId = languageId,
                difficulty = difficulty
            )
        )
    }

    override suspend fun joinRoom(roomId: String) {
        battleApi.sendMessage(
            OutgoingBattleMessage.JoinRoom(roomId = roomId)
        )
    }

    override suspend fun toggleReady(roomId: String, isReady: Boolean) {
        battleApi.sendMessage(
            OutgoingBattleMessage.PlayerReady(
                roomId = roomId,
                isReady = isReady
            )
        )
    }

    override suspend fun submitCode(roomId: String, code: String) {
        battleApi.sendMessage(
            OutgoingBattleMessage.SubmitCode(
                roomId = roomId,
                code = code
            )
        )
    }

    override suspend fun leaveRoom(roomId: String) {
        battleApi.sendMessage(
            OutgoingBattleMessage.LeaveRoom(roomId = roomId)
        )
    }

    override fun getMessages(): Flow<IncomingBattleMessage> = battleApi.getMessages()

    override fun getConnectionState(): Flow<BattleConnectionState> = battleApi.getConnectionState()
}