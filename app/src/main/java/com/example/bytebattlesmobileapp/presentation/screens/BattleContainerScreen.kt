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
import androidx.compose.runtime.remember
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

    // Определяем, что показывать
    when {
        uiState.battleState is BattleRoomState.Finished -> {
            // Можно показать отдельный экран результатов
            // или просто вернуться на главный через несколько секунд
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
        uiState.roomId.isEmpty() || !uiState.isConnected && uiState.taskId!!.isEmpty() -> {
            BattleScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    onNavigateBack()
                },
                onNavigateTrain = {},
                viewModel = viewModel,
            )
        }
        uiState.roomId.isNotEmpty() || uiState.isConnected -> {
            BattleLobbyScreen(
                onNavigateBack = {
                    viewModel.leaveRoom()
                    viewModel.disconnect()
                    onNavigateBack()
                },
                onNavigateToGame = onNavigateToGame,
                viewModel = viewModel
            )
        }
        else -> {
            TrainBattleScreen(
                onNavigateBack = {
                    viewModel.leaveRoom()
                    viewModel.disconnect()
                    onNavigateBack()
                },
                taskId = uiState.taskId ?: "",
                roomId = uiState.roomId
            )
        }
    }
}