package com.example.bytebattlesmobileapp.presentation.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val connectBattleUseCase: ConnectBattleUseCase,
    private val disconnectBattleUseCase: DisconnectBattleUseCase,
    private val createRoomUseCase: CreateRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val toggleReadyUseCase: ToggleReadyUseCase,
    private val submitCodeUseCase: SubmitCodeUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val getBattleMessagesUseCase: GetBattleMessagesUseCase,
    private val getBattleConnectionStateUseCase: GetBattleConnectionStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BattleUiState())
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<IncomingBattleMessage>>(emptyList())
    val messages: StateFlow<List<IncomingBattleMessage>> = _messages.asStateFlow()

    init {
        observeMessages()
        observeConnectionState()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getBattleMessagesUseCase()
                .collect { message ->
                    _messages.update { it + message }
                    updateUiState(message)
                }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            getBattleConnectionStateUseCase()
                .collect { connectionState ->
                    _uiState.update {
                        it.copy(
                            isConnected = connectionState.isConnected,
                            connectionError = connectionState.error
                        )
                    }
                }
        }
    }

    private fun updateUiState(message: IncomingBattleMessage) {
        when (message) {
            is IncomingBattleMessage.Connected -> {
                _uiState.update {
                    it.copy(playerId = message.playerId)
                }
            }
            is IncomingBattleMessage.RoomCreated -> {
                _uiState.update {
                    it.copy(
                        currentRoomId = message.roomId,
                        currentRoomName = message.roomName
                    )
                }
            }
            is IncomingBattleMessage.JoinedRoom -> {
                _uiState.update {
                    it.copy(
                        currentRoomId = message.roomId,
                        currentRoomName = message.roomName,
                        participantsCount = message.participants,
                        canStart = message.canStart
                    )
                }
            }
            is IncomingBattleMessage.PlayerJoined -> {
                _uiState.update {
                    it.copy(participantsCount = message.participants)
                }
            }
            is IncomingBattleMessage.PlayerLeft -> {
                _uiState.update {
                    it.copy(participantsCount = message.participants)
                }
            }
            is IncomingBattleMessage.RoomStatus -> {
                _uiState.update {
                    it.copy(
                        participantsCount = message.participantCount,
                        readyCount = message.readyCount,
                        canStart = message.canStart
                    )
                }
            }
            is IncomingBattleMessage.PlayerReadySet -> {
                _uiState.update {
                    it.copy(isReady = message.isReady)
                }
            }
            is IncomingBattleMessage.GameStarted -> {
                _uiState.update {
                    it.copy(
                        isGameStarted = true,
                        isReady = false
                    )
                }
            }
            is IncomingBattleMessage.LeftRoom -> {
                _uiState.update {
                    it.copy(
                        currentRoomId = "",
                        currentRoomName = "",
                        participantsCount = 0,
                        readyCount = 0,
                        isReady = false,
                        isGameStarted = false,
                        canStart = false
                    )
                }
            }
            is IncomingBattleMessage.BattleWon, is IncomingBattleMessage.BattleFinished -> {
                _uiState.update {
                    it.copy(
                        currentRoomId = "",
                        currentRoomName = "",
                        participantsCount = 0,
                        readyCount = 0,
                        isReady = false,
                        isGameStarted = false,
                        canStart = false
                    )
                }
            }
            else -> {}
        }
    }

    fun connect(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = connectBattleUseCase(token)
            _uiState.update { it.copy(isLoading = false) }

            if (result.isFailure) {
                _uiState.update {
                    it.copy(connectionError = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            disconnectBattleUseCase()
        }
    }

    fun createRoom(roomName: String, languageId: String, difficulty: String) {
        viewModelScope.launch {
            createRoomUseCase(roomName, languageId, difficulty)
        }
    }

    fun joinRoom(roomId: String) {
        viewModelScope.launch {
            joinRoomUseCase(roomId)
        }
    }

    fun toggleReady() {
        val currentRoomId = _uiState.value.currentRoomId
        val isReady = _uiState.value.isReady

        if (currentRoomId.isNotEmpty()) {
            viewModelScope.launch {
                toggleReadyUseCase(currentRoomId, !isReady)
            }
        }
    }

    fun submitCode(code: String) {
        val currentRoomId = _uiState.value.currentRoomId

        if (currentRoomId.isNotEmpty()) {
            viewModelScope.launch {
                submitCodeUseCase(currentRoomId, code)
            }
        }
    }

    fun leaveRoom() {
        val currentRoomId = _uiState.value.currentRoomId

        if (currentRoomId.isNotEmpty()) {
            viewModelScope.launch {
                leaveRoomUseCase(currentRoomId)
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    fun clearError() {
        _uiState.update { it.copy(connectionError = null) }
    }
}

data class BattleUiState(
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val connectionError: String? = null,
    val playerId: String = "",
    val currentRoomId: String = "",
    val currentRoomName: String = "",
    val participantsCount: Int = 0,
    val readyCount: Int = 0,
    val isReady: Boolean = false,
    val isGameStarted: Boolean = false,
    val canStart: Boolean = false
)