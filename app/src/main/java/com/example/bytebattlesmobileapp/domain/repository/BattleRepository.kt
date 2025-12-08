package com.example.bytebattlesmobileapp.domain.repository

import com.example.bytebattlesmobileapp.domain.model.BattleDifficulty
import com.example.bytebattlesmobileapp.domain.model.BattleResult
import com.example.bytebattlesmobileapp.domain.model.BattleRoom
import com.example.bytebattlesmobileapp.domain.model.CodeSubmission
import java.util.UUID

interface BattleRepository {
    suspend fun createBattleRoom(name: String, languageId: UUID, difficulty: BattleDifficulty): BattleRoom
    suspend fun joinBattleRoom(roomId: UUID): BattleRoom
    suspend fun getBattleRoomStatus(roomId: UUID): BattleRoom
    suspend fun toggleReadyStatus(roomId: UUID, isReady: Boolean)
    suspend fun submitBattleCode(roomId: UUID, taskId: UUID, code: String, languageId: UUID): CodeSubmission
    suspend fun leaveBattleRoom(roomId: UUID)
    suspend fun getBattleResult(battleId: UUID): BattleResult
    suspend fun getActiveBattleRooms(): List<BattleRoom>
    suspend fun connectToBattleWebSocket(roomId: UUID): Boolean
    suspend fun disconnectFromBattleWebSocket()
}