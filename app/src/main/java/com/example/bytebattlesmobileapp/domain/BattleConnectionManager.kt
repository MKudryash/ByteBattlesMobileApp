package com.example.bytebattlesmobileapp.domain

import com.example.bytebattlesmobileapp.data.network.BattleApiService
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.data.network.BattleConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BattleConnectionManager @Inject constructor(
    private val battleApiService: BattleApiService
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _messages = MutableSharedFlow<IncomingBattleMessage>()
    val messages: SharedFlow<IncomingBattleMessage> = _messages.asSharedFlow()

    private val _connectionState = MutableStateFlow<BattleConnectionState>(BattleConnectionState())
    val connectionState: StateFlow<BattleConnectionState> = _connectionState

    init {
        observeMessages()
    }

    private fun observeMessages() {
        scope.launch {
            battleApiService.getMessages().collect { message ->
                _messages.emit(message)
            }
        }
    }

    suspend fun connect(token: String? = null) = battleApiService.connect()

    suspend fun disconnect() = battleApiService.disconnect()

    suspend fun sendMessage(message: com.example.bytebattlesmobileapp.data.network.OutgoingBattleMessage) {
        battleApiService.sendMessage(message)
    }
}