package com.example.bytebattlesmobileapp.domain.usecase


import com.example.bytebattlesmobileapp.data.network.BattleConnectionState
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.domain.repository.BattleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectBattleUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(token: String) = repository.connect(token)
}

class DisconnectBattleUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke() = repository.disconnect()
}

class CreateRoomUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(roomName: String, languageId: String, difficulty: String) =
        repository.createRoom(roomName, languageId, difficulty)
}

class JoinRoomUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(roomId: String) = repository.joinRoom(roomId)
}

class ToggleReadyUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(roomId: String, isReady: Boolean) =
        repository.toggleReady(roomId, isReady)
}

class SubmitCodeUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(roomId: String, code: String) =
        repository.submitCode(roomId, code)
}

class LeaveRoomUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    suspend operator fun invoke(roomId: String) = repository.leaveRoom(roomId)
}

class GetBattleMessagesUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    operator fun invoke(): Flow<IncomingBattleMessage> = repository.getMessages()
}

class GetBattleConnectionStateUseCase @Inject constructor(
    private val repository: BattleRepository
) {
    operator fun invoke(): Flow<BattleConnectionState> = repository.getConnectionState()
}