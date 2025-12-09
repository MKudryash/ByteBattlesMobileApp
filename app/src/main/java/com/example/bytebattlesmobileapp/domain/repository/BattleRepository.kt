package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.data.network.BattleConnectionState
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import kotlinx.coroutines.flow.Flow

interface BattleRepository {
    suspend fun connect(token: String): Result<Unit>
    suspend fun disconnect()
    suspend fun createRoom(roomName: String, languageId: String, difficulty: String)
    suspend fun joinRoom(roomId: String)
    suspend fun toggleReady(roomId: String, isReady: Boolean)
    suspend fun submitCode(roomId: String, code: String)
    suspend fun leaveRoom(roomId: String)
    fun getMessages(): Flow<IncomingBattleMessage>
    fun getConnectionState(): Flow<BattleConnectionState>
}