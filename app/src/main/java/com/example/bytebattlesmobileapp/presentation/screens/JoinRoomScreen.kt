// JoinRoomScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.BackArrow
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel

@Composable
fun JoinRoomScreen(
    onNavigateBack: () -> Unit,
    viewModel: BattleLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var roomId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Следим за состоянием подключения
    LaunchedEffect(uiState.isConnected) {
        if (!uiState.isConnected) {
            errorMessage = "Нет подключения к серверу"
        }
    }

    // Следим за состоянием загрузки
    LaunchedEffect(uiState.isLoading) {
        isLoading = uiState.isLoading
    }

    // Следим за ошибками
    LaunchedEffect(uiState.connectionError) {
        uiState.connectionError?.let { error ->
            errorMessage = error
        }
    }

    // Если комната создана/присоединена, очищаем состояние
    LaunchedEffect(uiState.roomId) {
        if (uiState.roomId.isNotEmpty()) {
            // Поле комнаты заполнится автоматически при успешном присоединении
        }
    }
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    var roomIdInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Следим за сообщениями об ошибках
    LaunchedEffect(uiState.connectionError, messages) {
        uiState.connectionError?.let { error ->
            if (error.isNotBlank() && error != "null") {
                errorMessage = error
                showError = true
            }
        }

        // Проверяем сообщение об ошибке присоединения
        messages.lastOrNull()?.let { message ->
            if (message is com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage.Error) {
                errorMessage = message.message
                showError = true
            }
        }
    }

    // Автоматический переход в лобби при успешном присоединении
    LaunchedEffect(uiState.roomId, uiState.isConnected) {
        if (uiState.roomId.isNotEmpty() && uiState.isConnected) {
            println("JoinRoomScreen: Successfully joined room! RoomId: ${uiState.roomId}")
            // Комната присоединена, показываем лобби (автоматически через BattleContainerScreen)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя часть с заголовком
            SingleRoundedCornerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 40.dp,
                bottomEnd = 40.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Иконка присоединения
                    Image(
                        painter = painterResource(R.drawable.man), // Создайте drawable join_icon
                        contentDescription = "Присоединиться",
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Присоединиться к комнате",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Статус подключения
                    ConnectionStatus(
                        isConnected = uiState.isConnected,
                        isLoading = uiState.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Основное содержимое
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Поле ввода ID комнаты
                CustomUnderlinedTextField(
                    value = roomId,
                    onValueChange = {
                        roomId = it
                        errorMessage = null // Очищаем ошибку при вводе
                    },
                    placeholder = "Введите ID комнаты",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Подсказка
                Text(
                    text = "ID комнаты - это уникальный код, который вам должен предоставить создатель комнаты",
                    color = Color(0xFFA0AEC0),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Показать ошибку если есть
                errorMessage?.let { error ->
                    ErrorMessage(
                        message = error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                }

                // Кнопка присоединения
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF5EC2C3),
                        strokeWidth = 4.dp
                    )
                } else {
                    ActionButton(
                        text = "Присоединиться".uppercase(),
                        onClick = {
                            if (roomId.isBlank()) {
                                errorMessage = "Введите ID комнаты"
                            } else if (!uiState.isConnected) {
                                errorMessage = "Нет подключения к серверу"
                            } else {
                                viewModel.joinRoom(roomId)
                            }
                        },
                        color = Color(0xFF5EC2C3),
                        enabled = roomId.isNotBlank() && uiState.isConnected && !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Альтернативные действия
                Text(
                    text = "Или",
                    color = Color(0xFFA0AEC0),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                ActionButton(
                    text = "Создать свою комнату".uppercase(),
                    onClick = { onNavigateBack() },
                    color = Color(0xFF3C4B60),
                )
            }
        }
        BackArrow({  if (!isLoading) {
            onNavigateBack()
        }},   Modifier.padding(10.dp))


    }
}

@Composable
fun ConnectionStatus(isConnected: Boolean, isLoading: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Индикатор статуса
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = when {
                        isLoading -> Color.Yellow
                        isConnected -> Color.Green
                        else -> Color.Red
                    },
                    shape = RoundedCornerShape(50)
                )
        )

        Text(
            text = when {
                isLoading -> "Подключение..."
                isConnected -> "Подключено к серверу"
                else -> "Не подключено"
            },
            color = when {
                isLoading -> Color.Yellow
                isConnected -> Color.Green
                else -> Color.Red
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Ошибка",
            tint = Color(0xFFF56565),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            color = Color(0xFFF56565),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
