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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.Header
import kotlinx.coroutines.delay

// Состояние комнаты для ожидания
sealed class BattleRoomState {
    object WaitingForPlayers : BattleRoomState()
    object ReadyCheck : BattleRoomState()
    object StartingGame : BattleRoomState()
    object GameStarted : BattleRoomState()
    data class Error(val message: String) : BattleRoomState()
}

// Модель участника
data class BattleParticipant(
    val id: String,
    val name: String,
    val isReady: Boolean = false,
    val isConnected: Boolean = true,
    val avatarRes: Int = R.drawable.csharp // Заглушка
)

@Composable
fun BattleLobbyScreen(
    battleName: String = "Кодовая дуэль",
    battleType: String = "1 vs 1",
    difficulty: String = "Средняя",
    language: String = "C#",
    roomCode: String = "ABC123",
    participants: List<BattleParticipant> = emptyList(),
    currentPlayerId: String = "player1",
    countdownSeconds: Int = 30,
    battleState: BattleRoomState = BattleRoomState.WaitingForPlayers,
    onReadyToggle: () -> Unit = {},
    onLeaveBattle: () -> Unit = {},
    onStartBattle: () -> Unit = {},
    onCopyCode: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var isCurrentPlayerReady by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(countdownSeconds) }

    // Запуск таймера только в состоянии ReadyCheck
    LaunchedEffect(battleState, countdown) {
        if (battleState is BattleRoomState.ReadyCheck && countdown > 0) {
            delay(1000)
            countdown--
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
                 {onNavigateBack()},
                "Битва: $battleName"
            )

            // Информация о комнате
            RoomInfoCard(
                battleType = battleType,
                difficulty = difficulty,
                language = language,
                roomCode = roomCode,
                onCopyCode = onCopyCode
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Состояние комнаты
            RoomStatusSection(
                battleState = battleState,
                countdown = countdown,
                participantsCount = participants.size
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Список участников
            ParticipantsList(
                participants = participants,
                currentPlayerId = currentPlayerId,
                battleState = battleState
            )

            // Кнопки действий в зависимости от состояния
            when (battleState) {
                is BattleRoomState.WaitingForPlayers -> {
                    WaitingActions(
                        canStart = participants.size >= 2,
                        onStartBattle = onStartBattle,
                        onLeaveBattle = onLeaveBattle
                    )
                }
                is BattleRoomState.ReadyCheck -> {
                    ReadyCheckActions(
                        isReady = isCurrentPlayerReady,
                        countdown = countdown,
                        onReadyToggle = {
                            isCurrentPlayerReady = !isCurrentPlayerReady
                            onReadyToggle()
                        },
                        onLeaveBattle = onLeaveBattle
                    )
                }
                is BattleRoomState.StartingGame -> {
                    StartingGameView()
                }
                is BattleRoomState.GameStarted -> {
                    GameStartedView(onNavigateBack = onNavigateBack)
                }
                is BattleRoomState.Error -> {
                    ErrorView(
                        errorMessage = (battleState as BattleRoomState.Error).message,
                        onLeaveBattle = onLeaveBattle
                    )
                }
            }
        }
    }
}

@Composable
fun RoomInfoCard(
    battleType: String,
    difficulty: String,
    language: String,
    roomCode: String,
    onCopyCode: (String) -> Unit
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
                        text = roomCode,
                        color = Color(0xFF53C2C3),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                    )

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Детали битвы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
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
                )
            }
        }
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
fun RoomStatusSection(
    battleState: BattleRoomState,
    countdown: Int,
    participantsCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (battleState) {
                is BattleRoomState.WaitingForPlayers -> Color(0x1AFF9800)
                is BattleRoomState.ReadyCheck -> Color(0x1A4CAF50)
                is BattleRoomState.StartingGame -> Color(0x1A2196F3)
                is BattleRoomState.GameStarted -> Color(0x1A9C27B0)
                is BattleRoomState.Error -> Color(0x1AF44336)
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
                    text = when (battleState) {
                        is BattleRoomState.WaitingForPlayers -> "Ожидание игроков..."
                        is BattleRoomState.ReadyCheck -> "Подтверждение готовности"
                        is BattleRoomState.StartingGame -> "Начинаем битву!"
                        is BattleRoomState.GameStarted -> "Битва началась!"
                        is BattleRoomState.Error -> "Ошибка"
                    },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (battleState) {
                        is BattleRoomState.WaitingForPlayers -> "Ждем еще ${2 - participantsCount} игрока"
                        is BattleRoomState.ReadyCheck -> "Осталось: ${countdown}с"
                        is BattleRoomState.StartingGame -> "Подготовка к задаче..."
                        is BattleRoomState.GameStarted -> "Пишите код!"
                        is BattleRoomState.Error -> (battleState as BattleRoomState.Error).message
                    },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            // Иконка состояния
            val icon = when (battleState) {
                is BattleRoomState.WaitingForPlayers -> painterResource(R.drawable.hourglass)
                is BattleRoomState.ReadyCheck -> painterResource(R.drawable.check)
                is BattleRoomState.StartingGame -> painterResource(R.drawable.user)
                is BattleRoomState.GameStarted ->  painterResource(R.drawable.user)
                is BattleRoomState.Error ->  painterResource(R.drawable.error)
            }

            val iconColor = when (battleState) {
                is BattleRoomState.WaitingForPlayers -> Color(0xFFFF9800)
                is BattleRoomState.ReadyCheck -> Color(0xFF4CAF50)
                is BattleRoomState.StartingGame -> Color(0xFF2196F3)
                is BattleRoomState.GameStarted -> Color(0xFF9C27B0)
                is BattleRoomState.Error -> Color(0xFFF44336)
            }

            Icon(
                painter = icon,
                contentDescription = "Статус",
                tint = iconColor,
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
                text = "Участники (${participants.size})",
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
                        EmptyParticipantSlot(index = index + 1)
                    }
                }
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
                    painter = painterResource(participant.avatarRes),
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
fun WaitingActions(
    canStart: Boolean,
    onStartBattle: () -> Unit,
    onLeaveBattle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
fun GameStartedView(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Начато",
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
            text = "Перенаправляем на экран решения задачи...",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка для возврата (обычно происходит автоматический переход)
        ActionButton(
            text = "ВЕРНУТЬСЯ",
            onClick = onNavigateBack,
            color = Color(0xFF53C2C3),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        )
    }
}

@Composable
fun ErrorView(
    errorMessage: String,
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
            imageVector = Icons.Default.Close,
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
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ActionButton(
            text = "ВЫЙТИ".uppercase(),
            onClick = onLeaveBattle,
            color = Color(0xFFF44336),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        )
    }
}

// Пример использования с ViewModel
@Composable
fun BattleLobbyScreenWithLogic(
    onNavigateToBattle: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Пример состояния - в реальном приложении это будет в ViewModel
    var battleState by remember { mutableStateOf<BattleRoomState>(BattleRoomState.WaitingForPlayers) }
    var participants by remember {
        mutableStateOf(
            listOf(
                BattleParticipant("player1", "Алексей К.", isReady = true, avatarRes = R.drawable.csharp),
                BattleParticipant("player2", "Мария С.", isReady = false, avatarRes = R.drawable.csharp)
            )
        )
    }
    var countdown by remember { mutableStateOf(30) }

    BattleLobbyScreen(
        battleName = "Кодовая дуэль",
        battleType = "1 vs 1",
        difficulty = "Средняя",
        language = "C#",
        roomCode = "ABC123",
        participants = participants,
        currentPlayerId = "player1",
        countdownSeconds = countdown,
        battleState = battleState,
        onReadyToggle = {
            // Отправка на сервер через WebSocket
            // webSocket.send("PlayerReady", ...)
        },
        onLeaveBattle = {
            // Отправка на сервер
            // webSocket.send("LeaveRoom", ...)
            onNavigateBack()
        },
        onStartBattle = {
            battleState = BattleRoomState.ReadyCheck
            // Отправка на сервер
            // webSocket.send("StartReadyCheck", ...)
        },
        onCopyCode = { code ->
            // Копирование в буфер обмена
            // clipboardManager.setText(AnnotatedString(code))
        },
        onNavigateBack = onNavigateBack
    )
}

@Preview
@Composable
fun BattleLobbyScreenPreview() {
    BattleLobbyScreen(
        battleName = "Кодовая дуэль",
        battleType = "1 vs 1",
        difficulty = "Средняя",
        language = "C#",
        roomCode = "ABC123",
        participants = listOf(
            BattleParticipant("player1", "Алексей К.", isReady = true),
            BattleParticipant("player2", "Мария С.", isReady = false)
        ),
        currentPlayerId = "player1",
        countdownSeconds = 25,
        battleState = BattleRoomState.ReadyCheck
    )
}

@Preview
@Composable
fun BattleLobbyWaitingPreview() {
    BattleLobbyScreen(
        battleName = "Турнир по алгоритмам",
        battleType = "Турнир",
        difficulty = "Сложная",
        language = "Python",
        roomCode = "XYZ789",
        participants = listOf(
            BattleParticipant("player1", "Иван П.", isReady = false)
        ),
        currentPlayerId = "player1",
        countdownSeconds = 30,
        battleState = BattleRoomState.WaitingForPlayers
    )
}