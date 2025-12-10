package com.example.bytebattlesmobileapp.data.network

import android.util.Log
import com.example.bytebattlesmobileapp.data.datasource.remote.TokenManager
import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BattleApiServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val json: Json,
    private val tokenManager: TokenManager
) : BattleApiService {

    private var session: DefaultClientWebSocketSession? = null
    private val _messages = MutableSharedFlow<IncomingBattleMessage>()
    private val _connectionState = MutableStateFlow(BattleConnectionState())
    private var receiveJob: Job? = null

    override suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Проверяем, не подключены ли уже
            if (session != null && _connectionState.value.isConnected && session?.isActive == true) {
                return@withContext Result.success(Unit)
            }

            // Закрываем предыдущее соединение, если есть
            session?.close()
            receiveJob?.cancel()

            // Получаем токен для авторизации
            val token = tokenManager.getAccessToken().firstOrNull()

            Log.d("BattleApiService", "Connecting to WebSocket with token: ${token?.take(20)}...")

            // Используем отдельную корутину для поддержания соединения
            client.webSocket(
                method = HttpMethod.Get,
                host = "api.m.hobbit1021.ru",
                path = "/api/battle",
                request = {
                    url {
                        protocol = URLProtocol.WSS
                    }

                    // Добавляем заголовок авторизации
                    token?.let {
                        header(HttpHeaders.Authorization, "Bearer $it")
                    }




                }
            ) {
                Log.d("BattleApiService", "WebSocket connection established")
                session = this

                // Обновляем состояние подключения
                _connectionState.value = BattleConnectionState(isConnected = true)

                // Запускаем обработку входящих сообщений
                receiveJob = launch {
                    try {
                        receiveMessages()
                    } catch (e: Exception) {
                        Log.e("BattleApiService", "Receive job failed", e)
                        handleDisconnection("Receive error: ${e.message}")
                    }
                }

                // Ждем завершения WebSocket сессии
                try {
                    // Этот блок будет выполняться пока соединение активно
                    // Просто ждем - соединение будет активно пока не будет закрыто
                    while (isActive) {
                        delay(1000)
                    }
                } catch (e: Exception) {
                    Log.d("BattleApiService", "WebSocket connection ended", e)
                } finally {
                    Log.d("BattleApiService", "WebSocket finally block")
                    handleDisconnection("Connection closed")
                }
            }

            Log.d("BattleApiService", "WebSocket block completed, returning success")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BattleApiService", "Connection error", e)
            _connectionState.value = BattleConnectionState(
                isConnected = false,
                error = "Connection failed: ${e.message}"
            )
            Result.failure(e)
        }
    }


    override suspend fun disconnect() {
        try {
            session?.close()
            receiveJob?.cancel()
        } catch (e: Exception) {
            Log.e("BattleApiService", "Error disconnecting", e)
        } finally {
            session = null
            receiveJob = null
            _connectionState.value = BattleConnectionState(isConnected = false)
            _messages.emit(IncomingBattleMessage.Disconnected)
        }
    }



    override suspend fun sendMessage(message: OutgoingBattleMessage) {
        val currentSession = session
        if (currentSession == null || !currentSession.isActive) {
            Log.e("BattleApiService", "Cannot send message: session is null or not active")

            // Пытаемся переподключиться
            connect()

            // Даем время на переподключение
            delay(1000)

            // Пробуем снова
            val newSession = session
            if (newSession == null || !newSession.isActive) {
                throw IllegalStateException("WebSocket not connected")
            }
        }

        val jsonMessage = when (message) {
            is OutgoingBattleMessage.CreateRoom -> {
                val msg = json.encodeToString(
                    CreateRoomMessage.serializer(),
                    CreateRoomMessage(
                        type = "CreateRoom",
                        roomName = message.roomName,
                        languageId = message.languageId,
                        difficulty = message.difficulty
                    )
                )
                Log.d("BattleApiService", "Sending CreateRoom message: $msg")
                msg
            }
            is OutgoingBattleMessage.JoinRoom -> {
                json.encodeToString(
                    JoinRoomMessage.serializer(),
                    JoinRoomMessage(
                        type = "JoinRoom",
                        roomId = message.roomId
                    )
                )
            }
            is OutgoingBattleMessage.LeaveRoom -> {
                json.encodeToString(
                    LeaveRoomMessage.serializer(),
                    LeaveRoomMessage(
                        type = "LeaveRoom",
                        roomId = message.roomId
                    )
                )
            }
            is OutgoingBattleMessage.PlayerReady -> {
                json.encodeToString(
                    PlayerReadyMessage.serializer(),
                    PlayerReadyMessage(
                        type = "PlayerReady",
                        roomId = message.roomId,
                        isReady = message.isReady
                    )
                )
            }
            is OutgoingBattleMessage.SubmitCode -> {
                json.encodeToString(
                    SubmitCodeMessage.serializer(),
                    SubmitCodeMessage(
                        type = "SubmitCode",
                        roomId = message.roomId,
                        code = message.code
                    )
                )
            }
        }

        try {
            Log.d("BattleApiService", "Attempting to send message...")

            // Используем безопасную отправку
            session?.let { wsSession ->
                // Проверяем активность сессии перед отправкой
                if (wsSession.isActive) {
                    wsSession.send(jsonMessage)
                    Log.d("BattleApiService", "Message sent successfully")
                } else {
                    throw IllegalStateException("WebSocket session is not active")
                }
            } ?: throw IllegalStateException("WebSocket session is null")

        } catch (e: CancellationException) {
            Log.e("BattleApiService", "Message sending cancelled", e)
            throw e
        } catch (e: Exception) {
            Log.e("BattleApiService", "Error sending message", e)
            // Пытаемся переподключиться при ошибке
            connect()
            throw e
        }
    }

    override fun getMessages(): Flow<IncomingBattleMessage> = _messages.asSharedFlow()

    override fun getConnectionState(): Flow<BattleConnectionState> = _connectionState.asStateFlow()

    private suspend fun DefaultClientWebSocketSession.receiveMessages() {
        Log.d("BattleApiService", "Starting message receiver")

        try {
            for (frame in incoming) {
                Log.d("BattleApiService", "Received frame: ${frame.frameType}")

                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        Log.d("BattleApiService", "Received text: $text")
                        try {
                            val message = parseBattleMessage(text)
                            _messages.emit(message)
                        } catch (e: Exception) {
                            Log.e("BattleApiService", "Error parsing message", e)
                        }
                    }
                    is Frame.Binary -> {
                        Log.d("BattleApiService", "Received binary frame")
                    }
                    is Frame.Ping -> {
                        Log.d("BattleApiService", "Received Ping, sending Pong")
                        try {
                            send(Frame.Pong(frame.buffer))
                        } catch (e: Exception) {
                            Log.e("BattleApiService", "Error sending Pong", e)
                        }
                    }
                    is Frame.Pong -> {
                        Log.d("BattleApiService", "Received Pong")
                    }
                    is Frame.Close -> {
                        val reason = frame.readReason()?.toString() ?: "No reason"
                        Log.d("BattleApiService", "Received Close frame: $reason")
                        handleDisconnection("Server closed connection: $reason")
                        break
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BattleApiService", "Error in receiveMessages", e)
            handleDisconnection("Receive error: ${e.message}")
        } finally {
            Log.d("BattleApiService", "Message receiver stopped")
        }
    }

    private suspend fun handleDisconnection(reason: String?) {
        session = null
        receiveJob = null
        _connectionState.value = BattleConnectionState(
            isConnected = false,
            error = reason
        )
        _messages.emit(IncomingBattleMessage.Disconnected)
    }

    private fun parseBattleMessage(jsonString: String): IncomingBattleMessage {
        return try {
            val jsonElement = json.parseToJsonElement(jsonString)
            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content ?: "unknown"

            when (type) {
                "connected" -> {
                    val playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: ""
                    val message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: ""
                    IncomingBattleMessage.Connected(playerId, message)
                }
                "room_created" -> {
                    IncomingBattleMessage.RoomCreated(
                        roomId = jsonElement.jsonObject["roomId"]?.jsonPrimitive?.content ?: "",
                        roomName = jsonElement.jsonObject["roomName"]?.jsonPrimitive?.content ?: "",
                        difficulty = jsonElement.jsonObject["difficulty"]?.jsonPrimitive?.content ?: "",
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "joined_room" -> {
                    IncomingBattleMessage.JoinedRoom(
                        roomId = jsonElement.jsonObject["roomId"]?.jsonPrimitive?.content ?: "",
                        roomName = jsonElement.jsonObject["roomName"]?.jsonPrimitive?.content ?: "",
                        participants = jsonElement.jsonObject["participants"]?.jsonPrimitive?.intOrNull ?: 0,
                        status = jsonElement.jsonObject["status"]?.jsonPrimitive?.content ?: "",
                        canStart = jsonElement.jsonObject["canStart"]?.jsonPrimitive?.booleanOrNull ?: false,
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "player_joined" -> {
                    IncomingBattleMessage.PlayerJoined(
                        playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: "",
                        participants = jsonElement.jsonObject["participants"]?.jsonPrimitive?.intOrNull ?: 0,
                        roomStatus = jsonElement.jsonObject["roomStatus"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "player_left" -> {
                    IncomingBattleMessage.PlayerLeft(
                        playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: "",
                        participants = jsonElement.jsonObject["participants"]?.jsonPrimitive?.intOrNull ?: 0
                    )
                }
                "room_status" -> {
                    IncomingBattleMessage.RoomStatus(
                        roomId = jsonElement.jsonObject["roomId"]?.jsonPrimitive?.content ?: "",
                        status = jsonElement.jsonObject["status"]?.jsonPrimitive?.content ?: "",
                        participantCount = jsonElement.jsonObject["participantCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        readyCount = jsonElement.jsonObject["readyCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        canStart = jsonElement.jsonObject["canStart"]?.jsonPrimitive?.booleanOrNull ?: false,
                        isActive = jsonElement.jsonObject["isActive"]?.jsonPrimitive?.booleanOrNull ?: false
                    )
                }
                "game_can_start" -> {
                    IncomingBattleMessage.GameCanStart(
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        countdown = jsonElement.jsonObject["countdown"]?.jsonPrimitive?.intOrNull ?: 0
                    )
                }
                "player_ready_changed" -> {
                    IncomingBattleMessage.PlayerReadyChanged(
                        playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: "",
                        isReady = jsonElement.jsonObject["isReady"]?.jsonPrimitive?.booleanOrNull ?: false,
                        readyCount = jsonElement.jsonObject["readyCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        totalPlayers = jsonElement.jsonObject["totalPlayers"]?.jsonPrimitive?.intOrNull ?: 0
                    )
                }
                "player_ready_set" -> {
                    IncomingBattleMessage.PlayerReadySet(
                        isReady = jsonElement.jsonObject["isReady"]?.jsonPrimitive?.booleanOrNull ?: false,
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "game_started" -> {
                    IncomingBattleMessage.GameStarted(
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        startTime = jsonElement.jsonObject["startTime"]?.jsonPrimitive?.content ?: "",
                        duration = jsonElement.jsonObject["duration"]?.jsonPrimitive?.intOrNull ?: 0,
                        taskId = jsonElement.jsonObject["taskId"]?.jsonPrimitive?.content,
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content
                    )
                }
                "readiness_timeout" -> {
                    IncomingBattleMessage.ReadinessTimeout(
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        readyCount = jsonElement.jsonObject["readyCount"]?.jsonPrimitive?.intOrNull ?: 0,
                        totalPlayers = jsonElement.jsonObject["totalPlayers"]?.jsonPrimitive?.intOrNull ?: 0
                    )
                }
                "code_submitted" -> {
                    IncomingBattleMessage.CodeSubmitted(
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "code_submitted_by_player" -> {
                    IncomingBattleMessage.CodeSubmittedByPlayer(
                        playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: "",
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "code_result" -> {
                    val resultElement = jsonElement.jsonObject["result"]
                    val testResultsElement = jsonElement.jsonObject["testResults"]

                    val result = CodeResultData(
                        status = resultElement?.jsonObject?.get("status")?.jsonPrimitive?.content ?: "",
                        passedTests = resultElement?.jsonObject?.get("passedTests")?.jsonPrimitive?.intOrNull ?: 0,
                        totalTests = resultElement?.jsonObject?.get("totalTests")?.jsonPrimitive?.intOrNull ?: 0,
                        executionTime = resultElement?.jsonObject?.get("executionTime")?.jsonPrimitive?.intOrNull ?: 0
                    )

                    val testResults = if (testResultsElement?.jsonArray != null) {
                        testResultsElement.jsonArray.map { testElement ->
                            TestResult(
                                status = testElement.jsonObject["status"]?.jsonPrimitive?.content ?: "",
                                input = testElement.jsonObject["input"]?.jsonPrimitive?.content ?: "",
                                expectedOutput = testElement.jsonObject["expectedOutput"]?.jsonPrimitive?.content ?: "",
                                actualOutput = testElement.jsonObject["actualOutput"]?.jsonPrimitive?.content ?: "",
                                executionTime = testElement.jsonObject["executionTime"]?.jsonPrimitive?.intOrNull ?: 0
                            )
                        }
                    } else {
                        null
                    }

                    IncomingBattleMessage.CodeResult(result, testResults)
                }
                "battle_won" -> {
                    IncomingBattleMessage.BattleWon(
                        winnerId = jsonElement.jsonObject["winnerId"]?.jsonPrimitive?.content ?: "",
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content ?: "",
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        timestamp = jsonElement.jsonObject["timestamp"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "battle_lost" -> {
                    IncomingBattleMessage.BattleLost(
                        winnerId = jsonElement.jsonObject["winnerId"]?.jsonPrimitive?.content ?: "",
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content ?: "",
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        timestamp = jsonElement.jsonObject["timestamp"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "battle_finished" -> {
                    IncomingBattleMessage.BattleFinished(
                        winnerId = jsonElement.jsonObject["winnerId"]?.jsonPrimitive?.content ?: "",
                        taskTitle = jsonElement.jsonObject["taskTitle"]?.jsonPrimitive?.content ?: "",
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: "",
                        timestamp = jsonElement.jsonObject["timestamp"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "left_room" -> {
                    IncomingBattleMessage.LeftRoom(
                        roomId = jsonElement.jsonObject["roomId"]?.jsonPrimitive?.content ?: ""
                    )
                }
                "player_disconnected" -> {
                    IncomingBattleMessage.PlayerDisconnected(
                        playerId = jsonElement.jsonObject["playerId"]?.jsonPrimitive?.content ?: "",
                        participants = jsonElement.jsonObject["participants"]?.jsonPrimitive?.intOrNull
                    )
                }
                "error" -> {
                    IncomingBattleMessage.Error(
                        message = jsonElement.jsonObject["message"]?.jsonPrimitive?.content ?: ""
                    )
                }
                else -> IncomingBattleMessage.Unknown(jsonString)
            }
        } catch (e: Exception) {
            Log.e("BattleApiService", "Error parsing message", e)
            IncomingBattleMessage.Error("Error parsing message: ${e.message}")
        }
    }
}

// Message classes for sending
@Serializable
data class CreateRoomMessage(
    val type: String = "CreateRoom",
    val roomName: String,
    val languageId: String,
    val difficulty: String
)

@Serializable
data class JoinRoomMessage(
    val type: String = "JoinRoom",
    val roomId: String
)

@Serializable
data class PlayerReadyMessage(
    val type: String = "PlayerReady",
    val roomId: String,
    val isReady: Boolean
)

@Serializable
data class SubmitCodeMessage(
    val type: String = "SubmitCode",
    val roomId: String,
    val code: String
)

@Serializable
data class LeaveRoomMessage(
    val type: String = "LeaveRoom",
    val roomId: String
)
