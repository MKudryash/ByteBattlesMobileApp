package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleParticipant
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleRoomState
import kotlinx.coroutines.delay
import android.widget.Toast

@Composable
fun BattleLobbyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGame: (String,String) -> Unit,
    viewModel: BattleLobbyViewModel = hiltViewModel()
) {
    val taskId by viewModel.taskId.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isCurrentPlayerReady by remember { mutableStateOf(uiState.isCurrentPlayerReady) }
    var countdown by remember { mutableStateOf(uiState.countdown) }

    // Обновляем состояние при изменении uiState
    LaunchedEffect(uiState.isCurrentPlayerReady) {
        isCurrentPlayerReady = uiState.isCurrentPlayerReady
    }

    LaunchedEffect(uiState.countdown) {
        countdown = uiState.countdown
    }
    // Добавьте этот эффект для инициализации подключения
    LaunchedEffect(Unit) {
        if (!uiState.isConnected && uiState.roomId.isEmpty()) {
            // Если мы попали в лобби без комнаты, пытаемся переподключиться
            viewModel.connect("Игрок")
        }
    }

    // Отладка состояния
    LaunchedEffect(uiState.participants) {
        println("BattleLobbyScreen: Participants updated: ${uiState.participants.size}")
        uiState.participants.forEachIndexed { index, participant ->
            println("Participant $index: $participant")
        }
    }

    // Запуск таймера только в состоянии ReadyCheck
    LaunchedEffect(uiState.battleState, countdown) {
        if (uiState.battleState is BattleRoomState.ReadyCheck && countdown > 0) {
            delay(1000)
            countdown--
        }
    }
    LaunchedEffect(uiState.participants, uiState.roomId, uiState.playerId) {
        println("=== BATTLE LOBBY DEBUG ===")
        println("Room ID: ${uiState.roomId}")
        println("Player ID: ${uiState.playerId}")
        println("Participants count: ${uiState.participants.size}")
        println("Participants: ${uiState.participants}")
        println("Participants count from state: ${uiState.participantsCount}")
        println("=== END DEBUG ===")
    }
    // Обработка ошибок соединения
    LaunchedEffect(uiState.connectionError) {
        uiState.connectionError?.let { error ->
            if (error.isNotBlank() && error != "null") {
                errorMessage = error
                showErrorDialog = true
            }
        }
    }

    // Обработка сообщений об ошибках
    LaunchedEffect(messages) {
        messages.lastOrNull()?.let { message ->
            if (message is com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage.Error) {
                errorMessage = message.message
                showErrorDialog = true
            }
        }
    }

    LaunchedEffect(uiState.battleState, uiState.countdown) {
        println("BattleLobbyScreen: State changed - battleState=${uiState.battleState}, countdown=${uiState.countdown}, readyCount=${uiState.readyCount}")
    }

    // Автоматический переход при начале игры
    LaunchedEffect(uiState.battleState) {
        if (uiState.battleState is BattleRoomState.GameStarted && taskId != null) {
            delay(2000)
            onNavigateToGame(taskId!!,uiState.roomId)
        }
    }

    // Показываем тост при получении roomId
    LaunchedEffect(uiState.roomId) {
        if (uiState.roomId.isNotEmpty()) {
            Toast.makeText(
                context,
                "Комната создана! Код: ${uiState.roomId.takeLast(6).uppercase()}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(
                onNavigateBack = {
                    viewModel.leaveRoom()
                    onNavigateBack()
                },
                textHeader = if (uiState.roomName.isNotEmpty()) {
                    "Битва: ${uiState.roomName}"
                } else {
                    "Создание битвы..."
                }
            )

            // Информация о комнате
            RoomInfoCard(
                battleType = "1 vs 1",
                difficulty = viewModel.roomParams?.difficulty ?: "Easy",
                language = "C", // TODO: Получать из данных языка
                roomCode = uiState.roomId.takeLast(6).uppercase(),
                onCopyCode = { code ->
                    clipboardManager.setText(AnnotatedString(code))
                    Toast.makeText(context, "Код скопирован!", Toast.LENGTH_SHORT).show()
                },
                showCopyButton = uiState.roomId.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Состояние комнаты
            RoomStatusSection(
                battleState = uiState.battleState,
                countdown = countdown,
                participantsCount = uiState.participants.size,
                readyCount = uiState.readyCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Список участников
            ParticipantsList(
                participants = uiState.participants,
                currentPlayerId = uiState.playerId,
                battleState = uiState.battleState
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки действий в зависимости от состояния
            when (uiState.battleState) {
                is BattleRoomState.NotConnected -> {
                    NotConnectedView(
                        isConnected = uiState.isConnected,
                        isLoading = uiState.isLoading,
                        onConnect = { viewModel.connect() },
                        onLeave = {
                            viewModel.disconnect()
                            onNavigateBack()
                        }
                    )
                }
                is BattleRoomState.WaitingForPlayers -> {
                    WaitingActions(
                        roomId = uiState.roomId,
                        canStart = uiState.participants.size >= 2,
                        onStartBattle = { viewModel.startReadyCheck() },
                        onLeaveBattle = {
                            viewModel.leaveRoom()
                            onNavigateBack()
                        },
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            Toast.makeText(context, "Код скопирован!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                is BattleRoomState.ReadyCheck -> {
                    ReadyCheckActions(
                        isReady = isCurrentPlayerReady,
                        countdown = countdown,
                        onReadyToggle = { viewModel.toggleReady() },
                        onLeaveBattle = {
                            viewModel.leaveRoom()
                            onNavigateBack()
                        }
                    )

                }
                is BattleRoomState.StartingGame -> {
                    StartingGameView()
                }
                is BattleRoomState.GameStarted -> {
                    GameStartedView(
                        taskId = taskId,
                        onNavigateToGame = { taskId?.let { onNavigateToGame(it,uiState.roomId) } },
                        onNavigateBack = onNavigateBack
                    )
                }
                is BattleRoomState.Error -> {
                    ErrorView(
                        errorMessage = (uiState.battleState as BattleRoomState.Error).message,
                        onRetry = {
                            viewModel.clearError()
                            if (uiState.roomId.isNotEmpty()) {
                                viewModel.joinRoom(uiState.roomId)
                            } else {
                                viewModel.createRoom()
                            }
                        },
                        onLeaveBattle = {
                            viewModel.leaveRoom()
                            onNavigateBack()
                        }
                    )
                }

                BattleRoomState.Finished -> {

                }

            }
        }

        // Показываем индикатор загрузки
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // Диалог ошибки
        if (showErrorDialog) {

        }
    }
}

@Composable
fun NotConnectedView(
    isConnected: Boolean,
    isLoading: Boolean,
    onConnect: () -> Unit,
    onLeave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isConnected) Icons.Default.Warning else Icons.Default.Warning,
            contentDescription = "Подключение",
            tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isConnected) "Подключение установлено" else "Нет подключения",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isConnected)
                "Подключаемся к серверу битв..."
            else
                "Проверьте подключение к интернету",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isConnected && !isLoading) {
            ActionButton(
                text = "ПОДКЛЮЧИТЬСЯ",
                onClick = onConnect,
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "НАЗАД".uppercase(),
            onClick = onLeave,
            color = Color(0xFFF44336),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        )
    }
}

@Composable
fun RoomInfoCard(
    battleType: String,
    difficulty: String,
    language: String,
    roomCode: String,
    onCopyCode: (String) -> Unit,
    showCopyButton: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A4659)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Код комнаты
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Код комнаты:",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (roomCode.isNotEmpty()) roomCode else "------",
                        color = if (roomCode.isNotEmpty()) Color(0xFF53C2C3) else Color.White.copy(alpha = 0.5f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                    )

                    if (showCopyButton && roomCode.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Копировать",
                            tint = Color(0xFF53C2C3),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onCopyCode(roomCode) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Детали битвы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                /*InfoItem(
                    title = "Тип",
                    value = battleType,
                    icon = painterResource(R.drawable.one_vs_one)
                )

                InfoItem(
                    title = "Сложность",
                    value = difficulty,
                    icon = painterResource(R.drawable.middle)
                )

                InfoItem(
                    title = "Язык",
                    value = language,
                    icon = painterResource(R.drawable.csharp)
                )*/
            }
        }
    }
}

@Composable
fun RoomStatusSection(
    battleState: BattleRoomState,
    countdown: Int,
    participantsCount: Int,
    readyCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (battleState) {
                is BattleRoomState.NotConnected -> Color(0x1A757575)
                is BattleRoomState.WaitingForPlayers -> Color(0x1AFF9800)
                is BattleRoomState.ReadyCheck -> Color(0x1A4CAF50)
                is BattleRoomState.StartingGame -> Color(0x1A2196F3)
                is BattleRoomState.GameStarted -> Color(0x1A9C27B0)
                is BattleRoomState.Error -> Color(0x1AF44336)
                BattleRoomState.Finished -> {
                    Color(0x1AF44336)
                }
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text =
                        when (battleState) {
                        is BattleRoomState.NotConnected -> "Подключение..."
                        is BattleRoomState.WaitingForPlayers -> "Ожидание игроков"
                        is BattleRoomState.ReadyCheck -> "Подтверждение готовности"
                        is BattleRoomState.StartingGame -> "Начинаем битву!"
                        is BattleRoomState.GameStarted -> "Битва началась!"
                        is BattleRoomState.Error -> "Ошибка"
                            BattleRoomState.Finished -> "Завершение"
                        },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (battleState) {
                        is BattleRoomState.NotConnected -> "Устанавливаем соединение..."
                        is BattleRoomState.WaitingForPlayers -> "Ждем еще ${2 - participantsCount} игрока"
                        is BattleRoomState.ReadyCheck -> "Готовы: $readyCount/2 • Осталось: ${countdown}с"
                        is BattleRoomState.StartingGame -> "Подготовка к задаче..."
                        is BattleRoomState.GameStarted -> "Пишите код!"
                        is BattleRoomState.Error -> (battleState as BattleRoomState.Error).message
                        BattleRoomState.Finished -> "Завершение"
                    },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            // Иконка состояния
            val (icon, color) = when (battleState) {
                is BattleRoomState.NotConnected -> painterResource(R.drawable.hourglass) to Color(0xFF757575)
                is BattleRoomState.WaitingForPlayers -> painterResource(R.drawable.hourglass) to Color(0xFFFF9800)
                is BattleRoomState.ReadyCheck -> painterResource(R.drawable.check) to Color(0xFF4CAF50)
                is BattleRoomState.StartingGame -> painterResource(R.drawable.hourglass) to Color(0xFF2196F3)
                is BattleRoomState.GameStarted -> painterResource(R.drawable.hourglass) to Color(0xFF9C27B0)
                is BattleRoomState.Error -> painterResource(R.drawable.error) to Color(0xFFF44336)
                BattleRoomState.Finished -> painterResource(R.drawable.check) to Color(0xFF4CAF50)
            }

            Icon(
                painter = icon,
                contentDescription = "Статус",
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ParticipantsList(
    participants: List<BattleParticipant>,
    currentPlayerId: String,
    battleState: BattleRoomState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A4659)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Участники (${participants.size}/2)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(participants) { participant ->
                   ParticipantItem(
                        participant = participant,
                        isCurrentPlayer = participant.id == currentPlayerId,
                        battleState = battleState
                    )
                }

                // Добавляем пустые слоты для ожидаемых игроков
                if (participants.size < 2) {
                    items(2 - participants.size) { index ->
                        EmptyParticipantSlot(index = index + participants.size + 1)
                    }
                }
            }
        }
    }
}

@Composable
fun WaitingActions(
    roomId: String,
    canStart: Boolean,
    onStartBattle: () -> Unit,
    onLeaveBattle: () -> Unit,
    onCopyCode: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (roomId.isNotEmpty()) {
            Text(
                text = "Поделитесь кодом комнаты с другом:",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = roomId.takeLast(6).uppercase(),
                    color = Color(0xFF53C2C3),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Поделиться кодом",
                    tint = Color(0xFF53C2C3),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onCopyCode(roomId) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (canStart) {
            ActionButton(
                text = "НАЧАТЬ ПОДГОТОВКУ".uppercase(),
                onClick = onStartBattle,
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        } else {
            Text(
                text = "Нужно минимум 2 игрока для начала",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        ActionButton(
            text = "ПОКИНУТЬ КОМНАТУ".uppercase(),
            onClick = onLeaveBattle,
            color = Color(0xFFF44336),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

@Composable
fun GameStartedView(
    taskId: String?,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.man),
            contentDescription = "Начало",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Битва началась!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (taskId != null)
                "Перенаправляем на решение задачи..."
            else
                "Ожидание задачи...",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (taskId != null) {
            ActionButton(
                text = "ПЕРЕЙТИ К РЕШЕНИЮ",
                onClick = onNavigateToGame,
                color = Color(0xFF4CAF50),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "ВЫЙТИ".uppercase(),
            onClick = onNavigateBack,
            color = Color(0xFFF44336),
        )
    }
}

@Composable
fun ErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    onLeaveBattle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Ошибка",
            tint = Color(0xFFF44336),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ошибка",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = errorMessage,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                text = "ПОВТОРИТЬ",
                onClick = onRetry,
                color = Color(0xFF4CAF50),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )

            ActionButton(
                text = "ВЫЙТИ",
                onClick = onLeaveBattle,
                color = Color(0xFFF44336),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
        }
    }
}

@Composable
@Preview
fun BattleLobbyScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(
                    onNavigateBack = {},
                    textHeader = "Битва: C Easy Battle"
                )

                RoomInfoCard(
                    battleType = "1 vs 1",
                    difficulty = "Easy",
                    language = "C",
                    roomCode = "ABCDEF",
                    onCopyCode = {},
                    showCopyButton = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoomStatusSection(
                    battleState = BattleRoomState.WaitingForPlayers,
                    countdown = 30,
                    participantsCount = 1,
                    readyCount = 0
                )

                Spacer(modifier = Modifier.height(16.dp))

                ParticipantsList(
                    participants = listOf(
                        BattleParticipant(
                            id = "player1",
                            name = "Игрок 1",
                            isReady = true,
                            isConnected = true
                        )
                    ),
                    currentPlayerId = "player1",
                    battleState = BattleRoomState.WaitingForPlayers
                )

                Spacer(modifier = Modifier.height(16.dp))

                WaitingActions(
                    roomId = "ABCDEF",
                    canStart = false,
                    onStartBattle = {},
                    onLeaveBattle = {},
                    onCopyCode = {}
                )
            }
        }
    }
}
@Composable
fun ParticipantItem(
    participant: BattleParticipant,
    isCurrentPlayer: Boolean,
    battleState: BattleRoomState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF53C2C3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_man),
                    contentDescription = "Аватар",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = participant.name + if (isCurrentPlayer) " (Вы)" else "",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Normal
                )

                Text(
                    text = if (participant.isConnected) "В сети" else "Не в сети",
                    color = if (participant.isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 12.sp
                )
            }
        }

        // Статус готовности
        when (battleState) {
            is BattleRoomState.ReadyCheck -> {
                if (participant.isReady) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Готов",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFFF9800), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            color = Color(0xFFFF9800),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else -> {
                // Иконка статуса соединения
                Icon(
                    imageVector = if (participant.isConnected) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "Статус",
                    tint = if (participant.isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyParticipantSlot(index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Пустой аватар
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C3646)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "?",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Ожидание игрока $index...",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 16.sp
                )

                Text(
                    text = "Присоединится по коду",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )
            }
        }

        // Иконка ожидания
        Icon(
            painter = painterResource(R.drawable.hourglass),
            contentDescription = "Ожидание",
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.painter.Painter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReadyCheckActions(
    isReady: Boolean,
    countdown: Int,
    onReadyToggle: () -> Unit,
    onLeaveBattle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Таймер
        Text(
            text = "00:${countdown.toString().padStart(2, '0')}",
            color = if (countdown > 10) Color.White else Color(0xFFF44336),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
        )

        Text(
            text = "Подтвердите готовность до истечения времени",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        // Кнопка готовности
        ActionButton(
            text = if (isReady) "ГОТОВ!" else "ПОДТВЕРДИТЬ ГОТОВНОСТЬ",
            onClick = onReadyToggle,
            color = if (isReady) Color(0xFF4CAF50) else Color(0xFFFF9800),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        ActionButton(
            text = "ВЫЙТИ".uppercase(),
            onClick = onLeaveBattle,
            color = Color(0xFFF44336),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        )
    }
}

@Composable
fun StartingGameView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Анимация загрузки (можно заменить на Lottie)
        CircularProgressIndicator(
            color = Color(0xFF53C2C3),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Начинаем битву!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Подготовка задачи и окружения...",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ошибка подключения")
        },
        text = {
            Text(errorMessage)
        },
        confirmButton = {
            Button(
                onClick = onRetry
            ) {
                Text("Повторить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Закрыть")
            }
        }
    )
}