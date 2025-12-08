package com.example.bytebattlesmobileapp.data.network

import com.example.bytebattlesmobileapp.data.network.dto.auth.JoinRoomRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleResultDto
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleRoomRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.BattleRoomResponse
import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionRequest
import com.example.bytebattlesmobileapp.data.network.dto.battle.CodeSubmissionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import java.util.UUID

class BattleApiServiceImpl(private val client: HttpClient) : BattleApiService {
    override suspend fun createRoom(request: BattleRoomRequest): BattleRoomResponse {
        return client.post("battle/create") {
            setBody(request)
        }.body()
    }

    override suspend fun joinRoom(request: JoinRoomRequest): BattleRoomResponse {
        return client.post("battle/join") {
            setBody(request)
        }.body()
    }

    override suspend fun getRoomStatus(roomId: UUID): BattleRoomResponse {
        return client.get("battle/room/${roomId}/status").body()
    }

    override suspend fun toggleReady(roomId: UUID, isReady: Boolean) {
        client.put("battle/room/${roomId}/ready") {
            parameter("isReady", isReady)
        }
    }

    override suspend fun submitCode(request: CodeSubmissionRequest): CodeSubmissionResponse {
        return client.post("battle/submit") {
            setBody(request)
        }.body()
    }

    override suspend fun leaveRoom(roomId: UUID) {
        client.delete("battle/room/${roomId}/leave")
    }

    override suspend fun getBattleResult(battleId: UUID): BattleResultDto {
        return client.get("battle/${battleId}/result").body()
    }

    override suspend fun getActiveBattles(): List<BattleRoomResponse> {
        return client.get("battle/active").body()
    }
}