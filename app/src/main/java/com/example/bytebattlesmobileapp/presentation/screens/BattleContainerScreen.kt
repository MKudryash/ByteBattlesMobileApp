// BattleContainerScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel
import kotlinx.coroutines.delay

@Composable
fun BattleContainerScreen(
    navController: NavHostController,
    initialRoomId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToGame: (String) -> Unit,
    viewModel: BattleLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskId by viewModel.taskId.collectAsStateWithLifecycle()
    // Если перешли с roomId (например, по ссылке или из других экранов)
    LaunchedEffect(initialRoomId) {
        initialRoomId?.let { roomId ->
            if (roomId.isNotEmpty() && uiState.roomId.isEmpty()) {
                viewModel.joinRoom(roomId)
            }
        }
    }

    // Автоматический переход в лобби при создании комнаты
    LaunchedEffect(uiState.roomId) {
        if (uiState.roomId.isNotEmpty() && uiState.isConnected) {
            // Уже находимся на экране битвы, просто показываем лобби
        }
    }

    // Автоматический переход к игре
    LaunchedEffect(uiState.battleState, taskId) {
        println("BattleContainerScreen: battleState=${uiState.battleState}, taskId=$taskId")

        if (uiState.battleState is com.example.bytebattlesmobileapp.presentation.viewmodel.BattleRoomState.GameStarted && taskId != null) {
            println("BattleContainerScreen: Game started! TaskId: $taskId")
            delay(1000) // Даем время на отображение сообщения
            onNavigateToGame(taskId!!)
        }
    }

    // Определяем, что показывать
    if (uiState.roomId.isEmpty() || !uiState.isConnected) {
        // Показываем экран создания комнаты
        BattleScreen(
            onNavigateBack = {
                viewModel.disconnect()
                onNavigateBack()
            },
            onNavigateTrain = onNavigateToGame,
            viewModel = viewModel,

        )
    } else {
        // Показываем лобби
        BattleLobbyScreen(
            onNavigateBack = {
                viewModel.leaveRoom()
                onNavigateBack()
            },
            onNavigateToGame = { taskId ->
                // Переход будет через LaunchedEffect выше
            },
            viewModel = viewModel
        )
    }
}