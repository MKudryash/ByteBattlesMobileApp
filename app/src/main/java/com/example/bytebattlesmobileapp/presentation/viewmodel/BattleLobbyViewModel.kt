// Исправленный BattleLobbyViewModel
package com.example.bytebattlesmobileapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BattleLobbyViewModel @Inject constructor(
    private val connectBattleUseCase: ConnectBattleUseCase,
    private val disconnectBattleUseCase: DisconnectBattleUseCase,
    private val createRoomUseCase: CreateRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val toggleReadyUseCase: ToggleReadyUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val getBattleMessagesUseCase: GetBattleMessagesUseCase,
    private val getBattleConnectionStateUseCase: GetBattleConnectionStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BattleLobbyUiState())
    val uiState: StateFlow<BattleLobbyUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<IncomingBattleMessage>>(emptyList())
    val messages: StateFlow<List<IncomingBattleMessage>> = _messages.asStateFlow()

    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId.asStateFlow()

    var roomParams: BattleRoomParams? = null

    init {
        observeMessages()
        observeConnectionState()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getBattleMessagesUseCase()
                .collect { message ->
                    _messages.update { it + message }
                    handleIncomingMessage(message)
                }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            getBattleConnectionStateUseCase()
                .collect { connectionState ->
                    println("BattleLobbyViewModel: Connection state changed - isConnected=${connectionState.isConnected}, error=${connectionState.error}")

                    _uiState.update {
                        it.copy(
                            isConnected = connectionState.isConnected,
                            connectionError = connectionState.error,
                            isLoading = if (connectionState.isConnected) false else it.isLoading
                        )
                    }

                    if (connectionState.isConnected && roomParams != null && _uiState.value.roomId.isEmpty()) {
                        createRoom()
                    }
                }
        }
    }

    private fun handleIncomingMessage(message: IncomingBattleMessage) {
        println("BattleLobbyViewModel: Handling message: $message")

        when (message) {
            is IncomingBattleMessage.Connected -> {
                _uiState.update {
                    it.copy(
                        playerId = message.playerId,
                        isLoading = false
                    )
                }
                println("BattleLobbyViewModel: Player ID set to: ${message.playerId}")
            }

            is IncomingBattleMessage.RoomCreated -> {
                val currentPlayer = BattleParticipant(
                    id = _uiState.value.playerId,
                    name = "Вы",
                    isReady = false,
                    isConnected = true
                )

                _uiState.update {
                    it.copy(
                        roomId = message.roomId,
                        roomName = message.roomName,
                        battleState = BattleRoomState.WaitingForPlayers,
                        participants = listOf(currentPlayer),
                        participantsCount = 1,
                        readyCount = 0,
                        isLoading = false
                    )
                }
                println("BattleLobbyViewModel: Room created. RoomId: ${message.roomId}")
            }

            is IncomingBattleMessage.JoinedRoom -> {
                val currentPlayer = BattleParticipant(
                    id = _uiState.value.playerId,
                    name = "Вы",
                    isReady = false,
                    isConnected = true
                )

                _uiState.update {
                    it.copy(
                        roomId = message.roomId,
                        roomName = message.roomName,
                        battleState = BattleRoomState.WaitingForPlayers,
                        participants = listOf(currentPlayer),
                        participantsCount = 1,
                        readyCount = 0,
                        isLoading = false
                    )
                }
            }

            is IncomingBattleMessage.PlayerJoined -> {
                val newParticipant = BattleParticipant(
                    id = message.playerId,
                    name = "Игрок ${message.playerId.takeLast(4)}",
                    isReady = false,
                    isConnected = true
                )

                _uiState.update { currentState ->
                    val participantExists = currentState.participants.any { it.id == message.playerId }
                    if (!participantExists) {
                        currentState.copy(
                            participants = currentState.participants + newParticipant,
                            participantsCount = message.participants
                        )
                    } else {
                        currentState.copy(participantsCount = message.participants)
                    }
                }
                println("BattleLobbyViewModel: Player joined. Total participants: ${_uiState.value.participants.size}")
            }

            is IncomingBattleMessage.PlayerLeft -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        participants = currentState.participants.filter { participant ->
                            participant.id != message.playerId
                        },
                        participantsCount = message.participants
                    )
                }
            }

            is IncomingBattleMessage.PlayerReadySet -> {
                _uiState.update { currentState ->
                    currentState.copy(isCurrentPlayerReady = message.isReady)
                }
            }

            is IncomingBattleMessage.PlayerReadyChanged -> {
                _uiState.update { currentState ->
                    val updatedParticipants = currentState.participants.map { participant ->
                        if (participant.id == message.playerId) {
                            participant.copy(isReady = message.isReady)
                        } else {
                            participant
                        }
                    }

                    currentState.copy(
                        participants = updatedParticipants,
                        readyCount = message.readyCount
                    )
                }
                println("BattleLobbyViewModel: Player ready changed. readyCount=${message.readyCount}")
            }

            is IncomingBattleMessage.RoomStatus -> {
                println("BattleLobbyViewModel: RoomStatus received - participants=${message.participantCount}, ready=${message.readyCount}")

                _uiState.update { currentState ->
                    if (currentState.participants.isEmpty() && message.participantCount > 0) {
                        val selfParticipant = BattleParticipant(
                            id = currentState.playerId,
                            name = "Вы",
                            isReady = false,
                            isConnected = true
                        )
                        currentState.copy(
                            participants = listOf(selfParticipant),
                            participantsCount = message.participantCount,
                            readyCount = message.readyCount
                        )
                    } else {
                        currentState.copy(
                            participantsCount = message.participantCount,
                            readyCount = message.readyCount
                        )
                    }
                }
            }

            // ВАЖНО: Добавляем обработку GameCanStart
            is IncomingBattleMessage.GameCanStart -> {
                println("BattleLobbyViewModel: GameCanStart received - countdown=${message.countdown}")
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.ReadyCheck,
                        countdown = message.countdown
                    )
                }
                // Начинаем обратный отсчет
                startCountdown(message.countdown)
            }

            // ВАЖНО: Добавляем обработку ReadinessTimeout
            is IncomingBattleMessage.ReadinessTimeout -> {
                println("BattleLobbyViewModel: ReadinessTimeout received - message=${message.message}, readyCount=${message.readyCount}")
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.WaitingForPlayers,
                        countdown = 0,
                        readyCount = message.readyCount
                    )
                }
                // Сбрасываем состояние готовности всех участников
                _uiState.update { currentState ->
                    currentState.copy(
                        participants = currentState.participants.map { it.copy(isReady = false) },
                        isCurrentPlayerReady = false
                    )
                }
            }

            is IncomingBattleMessage.GameStarted -> {
                println("BattleLobbyViewModel: Game started! taskId=${message.taskId}, duration=${message.duration}")

                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.GameStarted,
                        gameDuration = message.duration
                    )
                }

                // ВАЖНО: Обновляем taskId
                _taskId.value = message.taskId

                println("BattleLobbyViewModel: taskId updated to: ${_taskId.value}")
            }

            is IncomingBattleMessage.LeftRoom -> {
                _uiState.update {
                    BattleLobbyUiState()
                }
            }

            is IncomingBattleMessage.Error -> {
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Error(message.message),
                        connectionError = message.message,
                        isLoading = false
                    )
                }
            }

            else -> {
                // Обработка других сообщений
            }
        }
    }

    private fun startCountdown(initialCountdown: Int) {
        viewModelScope.launch {
            var currentCountdown = initialCountdown
            while (currentCountdown > 0 &&
                _uiState.value.battleState is BattleRoomState.ReadyCheck) {
                delay(1000)
                currentCountdown--
                _uiState.update { it.copy(countdown = currentCountdown) }
                println("BattleLobbyViewModel: Countdown: $currentCountdown")
            }

            // Если таймер истек и мы все еще в состоянии ReadyCheck
            if (_uiState.value.battleState is BattleRoomState.ReadyCheck && currentCountdown == 0) {
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.WaitingForPlayers,
                        countdown = 0
                    )
                }
            }
        }
    }

    fun connect(token: String? = null) {
        viewModelScope.launch {
            println("BattleLobbyViewModel: Starting connection...")
            _uiState.update { it.copy(isLoading = true, connectionError = null) }

            try {
                val result = connectBattleUseCase(token ?: "")
                println("BattleLobbyViewModel: Connect use case result: $result")

                if (result.isFailure) {
                    _uiState.update {
                        it.copy(
                            battleState = BattleRoomState.Error("Ошибка подключения"),
                            connectionError = result.exceptionOrNull()?.message ?: "Неизвестная ошибка",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Error("Исключение при подключении"),
                        connectionError = e.message ?: "Неизвестная ошибка",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            disconnectBattleUseCase()
        }
    }

    fun createRoom() {
        roomParams?.let { params ->
            viewModelScope.launch {
                println("BattleLobbyViewModel: Creating room with params: $params")
                println("Current state: isConnected=${_uiState.value.isConnected}, roomId=${_uiState.value.roomId}")

                if (!_uiState.value.isConnected) {
                    _uiState.update {
                        it.copy(
                            battleState = BattleRoomState.Error("Нет подключения"),
                            connectionError = "Сначала подключитесь к серверу",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                try {
                    _uiState.update { it.copy(isLoading = true, connectionError = null) }

                    val result = createRoomUseCase(
                        roomName = params.roomName,
                        languageId = params.languageId,
                        difficulty = params.difficulty
                    )

                    println("BattleLobbyViewModel: Create room result: $result")

                    if (result==null) {
                        _uiState.update {
                            it.copy(
                                battleState = BattleRoomState.Error("Ошибка создания комнаты"),
                                connectionError =  "Неизвестная ошибка",
                                isLoading = false
                            )
                        }
                    } else {
                        // Не сбрасываем isLoading здесь - он сбросится при получении RoomCreated
                        println("BattleLobbyViewModel: Room creation request sent successfully")
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            battleState = BattleRoomState.Error("Исключение при создании комнаты"),
                            connectionError = e.message ?: "Неизвестная ошибка",
                            isLoading = false
                        )
                    }
                }
            }
        } ?: run {
            println("BattleLobbyViewModel: No roomParams set!")
            _uiState.update {
                it.copy(
                    battleState = BattleRoomState.Error("Параметры комнаты не заданы"),
                    connectionError = "Укажите язык и сложность"
                )
            }
        }
    }

    fun joinRoom(roomId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                joinRoomUseCase(roomId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        battleState = BattleRoomState.Error("Ошибка входа в комнату")
                    )
                }
            }
        }
    }

    fun toggleReady() {
        val roomId = _uiState.value.roomId
        val isReady = _uiState.value.isCurrentPlayerReady

        if (roomId.isNotEmpty()) {
            viewModelScope.launch {
                toggleReadyUseCase(roomId, !isReady)
            }
        }
    }

    fun leaveRoom() {
        val roomId = _uiState.value.roomId

        if (roomId.isNotEmpty()) {
            viewModelScope.launch {
                leaveRoomUseCase(roomId)
                // Сбрасываем состояние после выхода
                _uiState.update { BattleLobbyUiState() }
            }
        }
    }

    fun startReadyCheck() {
        // В реальном приложении это будет сообщение на сервер
        _uiState.update {
            it.copy(battleState = BattleRoomState.ReadyCheck, countdown = 30)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(connectionError = null) }
    }

    fun getCurrentTaskId(): String? = _taskId.value
}
data class BattleRoomParams (
    val roomName: String,
    val languageId: String,
    val difficulty: String
)

// Обновленный BattleLobbyUiState с начальным состоянием
data class BattleLobbyUiState(
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val connectionError: String? = null,
    val playerId: String = "",
    val roomId: String = "",
    val roomName: String = "",
    val participants: List<BattleParticipant> = emptyList(),
    val participantsCount: Int = 0,
    val readyCount: Int = 0,
    val isCurrentPlayerReady: Boolean = false,
    val battleState: BattleRoomState = BattleRoomState.NotConnected, // Изменили начальное состояние
    val countdown: Int = 30,
    val gameDuration: Int = 0,
    val taskId: String? = null,
)

// Добавляем новое состояние для начального статуса
sealed class BattleRoomState {
    object NotConnected : BattleRoomState() // Добавляем новое состояние
    object WaitingForPlayers : BattleRoomState()
    object ReadyCheck : BattleRoomState()
    object StartingGame : BattleRoomState()
    object GameStarted : BattleRoomState()
    data class Error(val message: String) : BattleRoomState()
}


// Обновленная модель участника
data class BattleParticipant(
    val id: String,
    val name: String,
    val isReady: Boolean = false,
    val isConnected: Boolean = true
)