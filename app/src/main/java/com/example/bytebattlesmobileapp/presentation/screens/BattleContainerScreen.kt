// BattleContainerScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleRoomState
import kotlinx.coroutines.delay


@Composable
fun BattleContainerScreen(
    navController: NavHostController,
    initialRoomId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToGame: (String, String) -> Unit,
    viewModel: BattleLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskId by viewModel.taskId.collectAsStateWithLifecycle()

    var isCreatingRoom by remember { mutableStateOf<Boolean?>(null) }

    // Отслеживаем, когда комната создана и мы должны перейти в лобби
    var shouldShowLobby by remember { mutableStateOf(false) }

    // Подключаемся при загрузке
    LaunchedEffect(Unit) {
        if (!uiState.isConnected) {
            viewModel.connect()
        }
    }

    // Обработка начального roomId
    LaunchedEffect(initialRoomId, uiState.isConnected) {
        if (initialRoomId != null && uiState.isConnected && isCreatingRoom == null) {
            // Автоматически присоединяемся к комнате если пришел roomId
            viewModel.joinRoom(initialRoomId)
            shouldShowLobby = true
        }
    }

    // Когда комната создана/присоединена, показываем лобби
    LaunchedEffect(uiState.roomId, uiState.isConnected) {
        if (uiState.roomId.isNotEmpty() && uiState.isConnected) {
            // Если комната есть и мы подключены, показываем лобби
            shouldShowLobby = true
        }
    }

    // Отслеживаем завершение битвы
    LaunchedEffect(uiState.battleState) {
        if (uiState.battleState is BattleRoomState.Finished) {
            // Задержка перед возвратом на главный
            delay(3000)
            viewModel.leaveRoom()
            viewModel.disconnect()
            onNavigateBack()
        }
    }

    // Отслеживаем переход в игру
    LaunchedEffect(uiState.battleState, taskId) {
        if (uiState.battleState is BattleRoomState.GameStarted && taskId != null) {
            // Переходим на экран игры
            onNavigateToGame(taskId!!, uiState.roomId)
        }
    }

    // Определяем, что показывать
    when {
        uiState.battleState is BattleRoomState.Finished -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C3646)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
                Text(
                    text = "Возвращаемся на главный...",
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Автоматический возврат
            LaunchedEffect(Unit) {
                delay(2000)
                onNavigateBack()
            }
        }

        // Если мы уже в комнате, показываем лобби
        shouldShowLobby && uiState.roomId.isNotEmpty() -> {
            BattleLobbyScreen(
                onNavigateBack = {
                    viewModel.leaveRoom()
                    viewModel.disconnect()
                    shouldShowLobby = false
                    isCreatingRoom = null
                },
                onNavigateToGame = onNavigateToGame,
                viewModel = viewModel
            )
        }

        // Если пользователь выбрал создание комнаты
        isCreatingRoom == true -> {
            BattleScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    onNavigateBack()
                },
                onNavigateTrain = {},
                viewModel = viewModel,
            )
        }

        // Если пользователь выбрал присоединение к комнате
        isCreatingRoom == false -> {
            JoinRoomScreen(
                onNavigateBack = { isCreatingRoom = null },
            )
        }

        // Экран выбора (по умолчанию)
        else -> {
            BattleSelectionScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    onNavigateBack()
                },
                onCreateRoomClick = { isCreatingRoom = true },
                onJoinRoomClick = { isCreatingRoom = false },
                viewModel = viewModel
            )
        }
    }
}
