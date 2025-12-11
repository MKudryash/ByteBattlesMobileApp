// Исправленный BattleLobbyViewModel
package com.example.bytebattlesmobileapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebattlesmobileapp.data.datasource.local.PlayerIdManager
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val submitCodeUseCase:SubmitCodeUseCase,
    private val getBattleMessagesUseCase: GetBattleMessagesUseCase,
    private val getBattleConnectionStateUseCase: GetBattleConnectionStateUseCase,
    private val playerIdManager: PlayerIdManager // Добавляем зависимость
) : ViewModel() {

    private val _uiState = MutableStateFlow(BattleLobbyUiState())
    val uiState: StateFlow<BattleLobbyUiState> = _uiState.asStateFlow()


    private val _gameTimer = MutableStateFlow<Int?>(null)
    val gameTimer: StateFlow<Int?> = _gameTimer.asStateFlow()

    private var gameTimerJob: Job? = null

    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

    private val _submitState =
        MutableStateFlow<SubmitSolutionStateBattle>(SubmitSolutionStateBattle.Idle)
    val submitState: StateFlow<SubmitSolutionStateBattle> = _submitState.asStateFlow()

    private val _messages = MutableStateFlow<List<IncomingBattleMessage>>(emptyList())
    val messages: StateFlow<List<IncomingBattleMessage>> = _messages.asStateFlow()

    private val _taskId = MutableStateFlow<String?>(null)
    val taskId: StateFlow<String?> = _taskId.asStateFlow()

    var roomParams: BattleRoomParams? = null

    init {
        restorePlayerId()
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
    fun updateCountdown(newCountdown: Int) {
        _uiState.update { it.copy(countdown = newCountdown) }
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
                println("BattleLobbyViewModel: Player ID set to: ${message.playerId}")
                // Сохраняем в SharedPreferences для дальнейшего использования
                savePlayerId(message.playerId)
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
                println("BattleLobbyViewModel: Joined to room - roomId=${message.roomId}, roomName=${message.roomName}, participants=${message.participants}")

                // Создаем себя как участника
                val currentPlayer = BattleParticipant(
                    id = _uiState.value.playerId,
                    name = "Вы",
                    isReady = false,
                    isConnected = true
                )

                // В сообщении JoinedRoom есть participants - общее количество участников
                // Если участников больше 1, значит есть другие игроки в комнате
                val totalParticipants = message.participants

                if (totalParticipants > 1) {
                    println("BattleLobbyViewModel: Room has ${totalParticipants} participants. Creating placeholder for other players.")

                    // Для других участников создаем заглушки
                    // Они будут заполнены реальными данными при получении PlayerReadyChanged
                    val otherParticipants = (2..totalParticipants).map { index ->
                        BattleParticipant(
                            id = "placeholder_$index", // Временный ID
                            name = "Игрок $index",
                            isReady = false,
                            isConnected = true
                        )
                    }

                    _uiState.update {
                        it.copy(
                            roomId = message.roomId,
                            roomName = message.roomName,
                            battleState = BattleRoomState.WaitingForPlayers,
                            participants = listOf(currentPlayer) + otherParticipants,
                            participantsCount = totalParticipants,
                            readyCount = 0,
                            isLoading = false
                        )
                    }
                } else {
                    // Только я в комнате
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
                println("BattleLobbyViewModel: Successfully joined room. RoomId: ${message.roomId}")
            }

            // Обновляем обработку PlayerReadyChanged чтобы обновлять реальных участников
            is IncomingBattleMessage.PlayerReadyChanged -> {
                println("BattleLobbyViewModel: PlayerReadyChanged - playerId=${message.playerId}, isReady=${message.isReady}, readyCount=${message.readyCount}, totalPlayers=${message.totalPlayers}")

                // Ищем участника с таким ID
                val participantExists = _uiState.value.participants.any { it.id == message.playerId }

                _uiState.update { currentState ->
                    if (participantExists) {
                        // Обновляем существующего участника
                        val updatedParticipants = currentState.participants.map { participant ->
                            if (participant.id == message.playerId) {
                                participant.copy(isReady = message.isReady)
                            } else {
                                participant
                            }
                        }

                        currentState.copy(
                            participants = updatedParticipants,
                            readyCount = message.readyCount,
                            participantsCount = message.totalPlayers
                        )
                    } else if (message.playerId != currentState.playerId) {
                        // Это новый участник (не я) - добавляем его
                        println("BattleLobbyViewModel: Adding new participant from PlayerReadyChanged: ${message.playerId}")

                        val newParticipant = BattleParticipant(
                            id = message.playerId,
                            name = "Игрок ${message.playerId.takeLast(4)}",
                            isReady = message.isReady,
                            isConnected = true
                        )

                        // Заменяем placeholder на реального участника
                        val updatedParticipants = if (currentState.participants.any { it.id.startsWith("placeholder_") }) {
                            // Заменяем первый placeholder
                            val placeholders = currentState.participants.filter { it.id.startsWith("placeholder_") }
                            if (placeholders.isNotEmpty()) {
                                val firstPlaceholder = placeholders.first()
                                currentState.participants.map {
                                    if (it.id == firstPlaceholder.id) newParticipant else it
                                }
                            } else {
                                currentState.participants + newParticipant
                            }
                        } else {
                            currentState.participants + newParticipant
                        }

                        currentState.copy(
                            participants = updatedParticipants,
                            readyCount = message.readyCount,
                            participantsCount = message.totalPlayers
                        )
                    } else {
                        // Это я - обновляем только счетчики
                        currentState.copy(
                            readyCount = message.readyCount,
                            participantsCount = message.totalPlayers
                        )
                    }
                }
                println("BattleLobbyViewModel: Player ready changed. readyCount=${message.readyCount}, totalPlayers=${message.totalPlayers}")
            }

            is IncomingBattleMessage.PlayerJoined -> {
                println("BattleLobbyViewModel: PlayerJoined - playerId=${message.playerId}, participants=${message.participants}")

                // Если это не я сам
                if (message.playerId != _uiState.value.playerId) {
                    val newParticipant = BattleParticipant(
                        id = message.playerId,
                        name = "Игрок ${message.playerId.takeLast(4)}",
                        isReady = false,
                        isConnected = true
                    )

                    _uiState.update { currentState ->
                        val participantExists = currentState.participants.any { it.id == message.playerId }
                        if (!participantExists) {
                            val updatedParticipants = currentState.participants + newParticipant
                            println("BattleLobbyViewModel: Added new participant. Total now: ${updatedParticipants.size}")
                            currentState.copy(
                                participants = updatedParticipants,
                                participantsCount = message.participants
                            )
                        } else {
                            println("BattleLobbyViewModel: Participant already exists, updating count")
                            currentState.copy(participantsCount = message.participants)
                        }
                    }
                } else {
                    // Это я сам - обновляем только количество
                    _uiState.update {
                        it.copy(participantsCount = message.participants)
                    }
                }
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
            is IncomingBattleMessage.CodeSubmitted -> {
                println("BattleLobbyViewModel: Code submitted - task: ${message.taskTitle}")
                // Можно обновить UI, показать что решение принято
            }

            is IncomingBattleMessage.CodeSubmittedByPlayer -> {
                println("BattleLobbyViewModel: Player ${message.playerId} submitted code")
                // Оповещение о том, что другой игрок отправил решение
            }

            is IncomingBattleMessage.CodeResult -> {
                println("BattleLobbyViewModel: Code result received - status=${message.result.status}")

                val resultMessage = when (message.result.status) {
                    "success" -> "Решение принято! Пройдено тестов: ${message.result.passedTests}/${message.result.totalTests}"
                    "wrong_answer" -> "Неправильный ответ. Пройдено тестов: ${message.result.passedTests}/${message.result.totalTests}"
                    "compile_error" -> "Ошибка компиляции"
                    "runtime_error" -> "Ошибка времени выполнения"
                    "time_limit_exceeded" -> "Превышено время выполнения"
                    else -> "Результат: ${message.result.status}"
                }

                // Показываем детали тестов если есть
                message.testResults?.let { testResults ->
                    println("Test results details:")
                    testResults.forEachIndexed { index, test ->
                        println("Test $index: ${test.status}, input=${test.input}, expected=${test.expectedOutput}, actual=${test.actualOutput}")
                    }
                }

                // Обновляем состояние
                _submitState.value = SubmitSolutionStateBattle.Success(resultMessage)

                // Автоматически сбрасываем через 5 секунд
                viewModelScope.launch {
                    delay(5000)
                    _submitState.value = SubmitSolutionStateBattle.Idle
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
                println("BattleLobbyViewModel: RoomStatus received - roomId=${message.roomId}, participants=${message.participantCount}, ready=${message.readyCount}, isActive=${message.isActive}")

                // Обработка статуса комнаты когда мы присоединились к существующей
                _uiState.update { currentState ->
                    // Если мы только что присоединились и список участников пустой
                    // или количество участников больше чем у нас в списке
                    if (currentState.participants.isEmpty() && message.participantCount > 0) {
                        println("BattleLobbyViewModel: Initializing participants from RoomStatus")

                        // Создаем себя
                        val selfParticipant = BattleParticipant(
                            id = currentState.playerId,
                            name = "Вы",
                            isReady = false,
                            isConnected = true
                        )

                        // Для других участников создаем заглушки
                        val otherParticipants = (1 until message.participantCount).map { index ->
                            BattleParticipant(
                                id = "unknown_$index",
                                name = "Игрок $index",
                                isReady = false,
                                isConnected = true
                            )
                        }

                        currentState.copy(
                            participants = listOf(selfParticipant) + otherParticipants,
                            participantsCount = message.participantCount,
                            readyCount = message.readyCount
                        )
                    } else {
                        // Обновляем счетчики
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
            is IncomingBattleMessage.BattleWon -> {
                println("BattleLobbyViewModel: Battle won by ${message.winnerId}")
                // Устанавливаем флаг завершения битвы
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Finished // Добавьте это состояние
                    )
                }
                stopGameTimer()
            }

            is IncomingBattleMessage.BattleFinished -> {
                println("BattleLobbyViewModel: Battle finished")
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Finished
                    )
                }
                stopGameTimer()
            }
            is IncomingBattleMessage.BattleLost -> {
                println("BattleLobbyViewModel: Battle lost. Winner: ${message.winnerId}")
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Finished
                    )
                }
                stopGameTimer()
            }
            is IncomingBattleMessage.ReadinessTimeout -> {
                println("BattleLobbyViewModel: ReadinessTimeout received - message=${message.message}, readyCount=${message.readyCount}")

                // Закрываем комнату при таймауте
                viewModelScope.launch {
                    leaveRoom()
                }

                // Показываем сообщение о закрытии
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.Error("Время ожидания истекло. Комната закрыта."),
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
                        gameDuration = message.duration,
                        gameTimeRemaining = message.duration
                    )
                }

                // ВАЖНО: Обновляем taskId
                _taskId.value = message.taskId

                startGameTimer(message.duration)

                // Устанавливаем флаг начала игры
                _gameStarted.value = true


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
    private fun startGameTimer(duration: Int) {
        stopGameTimer() // Останавливаем предыдущий таймер если был

        gameTimerJob = viewModelScope.launch {
            var timeRemaining = duration

            while (timeRemaining > 0 &&
                _uiState.value.battleState == BattleRoomState.GameStarted) {

                delay(1000)
                timeRemaining--

                // ОБНОВЛЯЕМ UIState вместо отдельного StateFlow
                _uiState.update { currentState ->
                    currentState.copy(
                        gameTimeRemaining = timeRemaining
                    )
                }

                println("BattleLobbyViewModel: Game time remaining: $timeRemaining seconds")

                // Если время вышло, завершаем игру
                if (timeRemaining == 0) {
                    println("BattleLobbyViewModel: Game time expired!")
                    handleGameTimeExpired()
                }
            }
        }
    }

    // Метод для остановки игрового таймера
    private fun stopGameTimer() {
        gameTimerJob?.cancel()
        gameTimerJob = null
        _gameTimer.value = null
    }

    // Обработка истечения времени игры
    private fun handleGameTimeExpired() {
        viewModelScope.launch {
            println("BattleLobbyViewModel: Game time expired, leaving room...")

            // Выходим из комнаты
            leaveRoom()

            // Отключаемся от WebSocket
            disconnect()

            // Обновляем состояние
            _uiState.update {
                it.copy(
                    battleState = BattleRoomState.Error("Время игры истекло"),
                    gameDuration = 0
                )
            }
        }
    }

    private fun startCountdown(initialCountdown: Int) {
        viewModelScope.launch {
            var currentCountdown = initialCountdown
            while (currentCountdown > 0 &&
                _uiState.value.battleState is BattleRoomState.ReadyCheck
            ) {
                delay(1000)
                currentCountdown--
                _uiState.update { it.copy(countdown = currentCountdown) }
                println("BattleLobbyViewModel: Countdown: $currentCountdown")
            }

            // Если таймер истек и мы все еще в состоянии ReadyCheck
            if (_uiState.value.battleState is BattleRoomState.ReadyCheck && currentCountdown == 0) {
                println("BattleLobbyViewModel: Timeout! Closing room...")

                // Обновляем состояние
                _uiState.update {
                    it.copy(
                        battleState = BattleRoomState.WaitingForPlayers,
                        countdown = 0
                    )
                }

                // Закрываем комнату
                leaveRoom()
            }
        }
    }
    private fun restorePlayerId() {
        val savedPlayerId = playerIdManager.getPlayerId()
        if (!savedPlayerId.isNullOrEmpty()) {
            _uiState.update { it.copy(playerId = savedPlayerId) }
            println("BattleLobbyViewModel: Restored playerId from storage: $savedPlayerId")
        } else {
            println("BattleLobbyViewModel: No saved playerId found")
        }
    }

    private fun savePlayerId(playerId: String) {
        // Сохраняем playerId в SharedPreferences
        playerIdManager.savePlayerId(playerId)
        println("BattleLobbyViewModel: Saved playerId to storage: $playerId")
    }
    fun  getPlayerId(): String = _uiState.value.playerId
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
                            connectionError = result.exceptionOrNull()?.message
                                ?: "Неизвестная ошибка",
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
            stopGameTimer()
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

                    if (result == null) {
                        _uiState.update {
                            it.copy(
                                battleState = BattleRoomState.Error("Ошибка создания комнаты"),
                                connectionError = "Неизвестная ошибка",
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
            println("BattleLobbyViewModel: Attempting to join room: $roomId")

            try {
                _uiState.update { it.copy(isLoading = true, connectionError = null) }

                if (!uiState.value.isConnected) {
                    println("BattleLobbyViewModel: Not connected, connecting first...")
                    connect()
                    // Ждем подключения
                    delay(1000)

                    if (!uiState.value.isConnected) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                battleState = BattleRoomState.Error("Не удалось подключиться"),
                                connectionError = "Ошибка подключения"
                            )
                        }
                        return@launch
                    }
                }

                println("BattleLobbyViewModel: Calling joinRoomUseCase with roomId: $roomId")
                val result = joinRoomUseCase(roomId)

                println("BattleLobbyViewModel: Join room result: $result")

                // Ждем немного чтобы получить сообщения от сервера
                delay(500)

                // Если после присоединения у нас все еще только 1 участник,
                // возможно нужно запросить статус комнаты явно
                if (_uiState.value.participantsCount <= 1) {
                    println("BattleLobbyViewModel: Only 1 participant after join, might need to request room status")
                    // Здесь можно отправить запрос на получение статуса комнаты
                    // если ваш протокол это поддерживает
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        battleState = BattleRoomState.Error("Ошибка входа в комнату: ${e.message}"),
                        connectionError = e.message ?: "Неизвестная ошибка"
                    )
                }
                println("BattleLobbyViewModel: Exception in joinRoom: ${e.message}")
            }
        }
    }


    fun submitSolutionViaWebSocket(code: String, roomId: String?) {
        viewModelScope.launch {
            val roomId = roomId

            if (roomId!!.isEmpty()) {
                _submitState.value = SubmitSolutionStateBattle.Error("Нет активной комнаты")
                return@launch
            }

            if (code.isBlank()) {
                _submitState.value = SubmitSolutionStateBattle.Error("Код не может быть пустым")
                return@launch
            }

            try {
                _submitState.value = SubmitSolutionStateBattle.Loading

                println("BattleLobbyViewModel: Submitting solution via WebSocket, roomId=$roomId, code length=${code.length}")

                // Используем use case для отправки кода
                submitCodeUseCase(
                    roomId = roomId,
                    code = code
                )

                // Если не было исключения, считаем успешным
                _submitState.value = SubmitSolutionStateBattle.Success("Решение отправлено на проверку")
                println("BattleLobbyViewModel: Solution submitted via WebSocket successfully")

                // Автоматически сбрасываем состояние через 3 секунды
                delay(3000)
                _submitState.value = SubmitSolutionStateBattle.Idle

            } catch (e: Exception) {
                _submitState.value = SubmitSolutionStateBattle.Error("Ошибка: ${e.message ?: "Неизвестная ошибка"}")
                println("BattleLobbyViewModel: Error submitting solution via WebSocket: ${e.message}")

                // Также сбрасываем состояние через 5 секунды при ошибке
                delay(5000)
                _submitState.value = SubmitSolutionStateBattle.Idle
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
                // Отправляем команду на сервер о выходе
                leaveRoomUseCase(roomId)
                stopGameTimer()
                // Сбрасываем состояние после выхода
                _uiState.update {
                    BattleLobbyUiState(
                        playerId = it.playerId, // Сохраняем playerId
                        isConnected = it.isConnected // Сохраняем состояние подключения
                    )
                }
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

    data class BattleRoomParams(
        val roomName: String,
        val languageId: String,
        val difficulty: String,
        val languageTitle: String? = "",
        val typeBattle: String? =""
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
        val countdown: Int = 60,
        val gameTimeRemaining: Int = 0,
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
        object Finished : BattleRoomState()
        data class Error(val message: String) : BattleRoomState()
    }

    sealed class SubmitSolutionStateBattle {
        object Idle : SubmitSolutionStateBattle()
        object Loading : SubmitSolutionStateBattle()
        data class Success(val message: String) : SubmitSolutionStateBattle()
        data class Error(val error: String) : SubmitSolutionStateBattle()
    }

    // Обновленная модель участника
    data class BattleParticipant(
        val id: String,
        val name: String,
        val isReady: Boolean = false,
        val isConnected: Boolean = true
    )
