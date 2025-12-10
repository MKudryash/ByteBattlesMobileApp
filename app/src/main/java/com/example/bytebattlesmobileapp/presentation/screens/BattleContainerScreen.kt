// BattleContainerScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

    // Сохраняем taskId в remember для использования после перехода
    val rememberedTaskId = remember(taskId) { taskId }

    // Если перешли с roomId
    LaunchedEffect(initialRoomId) {
        initialRoomId?.let { roomId ->
            if (roomId.isNotEmpty() && uiState.roomId.isEmpty()) {
                viewModel.joinRoom(roomId)
            }
        }
    }

    // Автоматический переход к игре при получении taskId
    LaunchedEffect(uiState.battleState, rememberedTaskId) {
        println("BattleContainerScreen: battleState=${uiState.battleState}, taskId=$rememberedTaskId")

        if (uiState.battleState is com.example.bytebattlesmobileapp.presentation.viewmodel.BattleRoomState.GameStarted && rememberedTaskId != null) {
            println("BattleContainerScreen: Game started! TaskId: $rememberedTaskId")

            // Даем время на отображение сообщения о начале игры
            delay(1500)

            // ВАЖНО: Не закрываем BattleContainerScreen полностью, только убираем его с экрана
            navController.navigate(Screen.TrainBattle.createRoute(rememberedTaskId)) {
                // НЕ используем popUpTo - оставляем BattleContainerScreen в стеке
                // Это сохраняет ViewModel и WebSocket соединение
            }
        }
    }

    // Определяем, что показывать
    if (uiState.roomId.isEmpty() || !uiState.isConnected) {
        BattleScreen(
            onNavigateBack = {
                // Оставляем WebSocket подключенным при возврате
                onNavigateBack()
            },
            onNavigateTrain = onNavigateToGame,
            viewModel = viewModel,
        )
    } else {
        BattleLobbyScreen(
            onNavigateBack = {
                // Просто выходим, WebSocket останется подключенным для TaskScreen
                onNavigateBack()
            },
            onNavigateToGame = onNavigateToGame,
            viewModel = viewModel
        )
    }
}