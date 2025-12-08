package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.auth.JoinRoomRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleResultDto
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleRoomRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleRoomResponse
import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionResponse
import java.util.UUID

interface BattleApiService {
    suspend fun createRoom(request: BattleRoomRequest): BattleRoomResponse
    suspend fun joinRoom(request: JoinRoomRequest): BattleRoomResponse
    suspend fun getRoomStatus(roomId: UUID): BattleRoomResponse
    suspend fun toggleReady(roomId: UUID, isReady: Boolean)
    suspend fun submitCode(request: CodeSubmissionRequest): CodeSubmissionResponse
    suspend fun leaveRoom(roomId: UUID)
    suspend fun getBattleResult(battleId: UUID): BattleResultDto
    suspend fun getActiveBattles(): List<BattleRoomResponse>
}