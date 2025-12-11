package com.example.bytebattlesmobileapp.presentation.screens

import CodeEditor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.data.network.IncomingBattleMessage
import com.example.bytebattlesmobileapp.domain.model.BattleParticipant
import com.example.bytebattlesmobileapp.domain.model.TestCase
import com.example.bytebattlesmobileapp.presentation.components.CustomInfoDialog
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.SubmitSolutionStateBattle
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.emptyList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainBattleScreen(
    onNavigateBack: () -> Unit,
    onNavigateMain:()-> Unit,
    taskId: String,
    viewModel: TaskViewModel = hiltViewModel(),
    battleLobbyViewModel: BattleLobbyViewModel = hiltViewModel(),
    roomId: String? = null,
) {
    Log.d("Train", "TaskId: $taskId")

    var nameTask by remember { mutableStateOf("Name of Task") }
    var initialCode by remember { mutableStateOf("// Write your code here") }
    var currentCode by remember { mutableStateOf<String?>(null) }
    var languageId by remember { mutableStateOf("") }
    var isCodeInitialized by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var testCases: List<TestCase> by remember { mutableStateOf(emptyList()) }

    var modalState by remember { mutableStateOf(ModalState.NONE) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    val submitState by battleLobbyViewModel.submitState.collectAsStateWithLifecycle()

    // Состояние для результата битвы
    var battleResult by remember { mutableStateOf<BattleResult?>(null) }

    val onNavigate = { destination: String ->
        modalState = ModalState.NONE
    }
    val taskState by viewModel.taskState.collectAsStateWithLifecycle()

    // Получаем состояние битвы
    val battleUiState by battleLobbyViewModel.uiState.collectAsStateWithLifecycle()
    val messages by battleLobbyViewModel.messages.collectAsStateWithLifecycle()

    // Загружаем задачу
    LaunchedEffect(taskId) {
        if (taskId.isNotEmpty()) {
            println("TrainBattleScreen: Loading task with id: $taskId")
            viewModel.getTaskById(UUID.fromString(taskId))
        }
    }


    LaunchedEffect(messages) {
        messages.lastOrNull()?.let { message ->
            println("TrainBattleScreen: Received battle message: $message")


            when (message) {
                is IncomingBattleMessage.BattleWon -> {
                    println("Hto win" + battleUiState.playerId)
                    val isCurrentPlayerWinner = message.winnerId == battleUiState.playerId
                    battleResult = BattleResult(
                        isWinner = isCurrentPlayerWinner,
                        taskTitle = message.taskTitle,
                        message = message.message
                    )
                    println("TrainBattleScreen: Battle result set - isWinner=$isCurrentPlayerWinner")
                }
                is IncomingBattleMessage.BattleLost -> {
                    println("=== BATTLE LOST DEBUG ===")
                    println("Winner ID from message: ${message.winnerId}")
                    println("Player ID from UI state: ${battleUiState.playerId}")


                    battleResult = BattleResult(
                        isWinner = false,
                        taskTitle = message.taskTitle,
                        message = message.message
                    )
                    println("TrainBattleScreen: Battle lost - message=${message.message}")
                    println("=== END DEBUG ===")
                }
                is IncomingBattleMessage.BattleFinished -> {

                    battleResult = BattleResult(
                        isWinner = false,
                        taskTitle = nameTask,
                        message = "Битва завершена"
                    )
                }
                else -> {}
            }
        }
    }

    // Отслеживаем состояние задачи
    LaunchedEffect(taskState) {
        when (taskState) {
            is TaskViewModel.TaskDetailState.Success -> {
                val task = (taskState as TaskViewModel.TaskDetailState.Success).task
                println("TrainBattleScreen: Task loaded: ${task.title}")
                nameTask = task.title
                description = task.description
                if (!isCodeInitialized && currentCode == null) {
                    currentCode = task.patternFunction
                    isCodeInitialized = true
                }
                languageId = task.language!!.title
                testCases = task.testCases
            }
            else -> {}
        }
    }

    // Функция для отправки решения
    val onSubmitSolution = {
        currentCode?.let { code ->
            battleLobbyViewModel.submitSolutionViaWebSocket(
                code = code,
                roomId = roomId
            )
        }
    }

    // Отслеживаем результат отправки
    LaunchedEffect(submitState) {
        when (submitState) {
            is SubmitSolutionStateBattle.Success -> {
                println("TrainBattleScreen: Solution submitted successfully")
            }
            else -> {}
        }
    }

    // Обработка закрытия результата битвы
    LaunchedEffect(battleResult) {
        battleResult?.let {

            delay(10000)
            battleResult = null
            // Возвращаемся на главный экран
            battleLobbyViewModel.leaveRoom()
            battleLobbyViewModel.disconnect()
            onNavigateBack()
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
                    battleLobbyViewModel.leaveRoom()
                    battleLobbyViewModel.disconnect()
                    onNavigateBack()
                },
                textHeader = nameTask
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Решение",
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                    )
                }

                currentCode?.let { code ->
                    CodeEditor(
                        language = getCodeLang(languageId),
                        initialCode = code,
                        onCodeChange = { newCode ->
                            currentCode = newCode
                        }
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }

        // Показываем кнопки действий только если нет результата битвы
        if (battleResult == null) {
            when (taskState) {
                is TaskViewModel.TaskDetailState.Success -> {
                    if (submitState !is TaskViewModel.SubmitSolutionState.Loading) {
                        BottomActionButtons(
                            onFinishClick = { modalState = ModalState.FINISH_TASK },
                            onInfoClick = { modalState = ModalState.TESTS_BOTTOM_SHEET },
                            onSubmitClick = {
                                modalState = ModalState.SUBMIT_CODE
                            }
                        )
                    }
                }
                else -> {}
            }
        }

        // Модальные окна
        ModalWindowsManager(
            modalState = modalState,
            onNavigateMain =onNavigateMain,
            onDismiss = { modalState = ModalState.NONE },
            onNavigate = onNavigate,
            bottomSheetState = bottomSheetState,
            scope = scope,
            onBottomSheetDismiss = { showBottomSheet = false },
            onSubmitSolution = { onSubmitSolution() },
            testCases = testCases,
            description = description
        )

        // Показываем результат битвы
        battleResult?.let { result ->
            BattleResultScreen(
                isWinner = result.isWinner,
                taskTitle = result.taskTitle,
                message = result.message,
                onDismiss = {
                    battleResult = null
                    battleLobbyViewModel.leaveRoom()
                    battleLobbyViewModel.disconnect()
                    onNavigateBack()
                }
            )
        }
    }
}


data class BattleResult(
    val isWinner: Boolean,
    val taskTitle: String,
    val message: String
)
@Composable
fun BattleStatusBar(
    roomId: String,
    timeRemaining: Int,
    participants: List<BattleParticipant>
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Битва продолжается",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Осталось времени: ${timeRemaining} сек",
                    color = Color(0xFFFF9800),
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Игроков: ${participants.size}",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalWindowsManager(
    modalState: ModalState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    bottomSheetState: SheetState,
    onNavigateMain:()-> Unit,
    scope: CoroutineScope,
    onBottomSheetDismiss: () -> Unit,
    onSubmitSolution: () -> Unit, // Добавлен параметр
    testCases: List<TestCase>,
    description: String
) {
    // Диалоги
    when (modalState) {
        ModalState.FINISH_TASK -> {
            CustomInfoDialog(
                showDialog = true,
                onDismiss = onDismiss,
                onNavigateToInfo = { onNavigateMain() },
                title = "Закончить?",
                text = "Завершить выполнение задачи?"
            )
        }

        ModalState.SUBMIT_CODE -> {
            CustomInfoDialog(
                showDialog = true,
                onDismiss = onDismiss,
                onNavigateToInfo = {
                    onSubmitSolution() // Вызываем функцию отправки
                },
                title = "Отправить код на проверку?",
                text = "Уверены?"
            )
        }

        ModalState.TESTS_BOTTOM_SHEET -> {
            TestsBottomSheet(
                sheetState = bottomSheetState,
                onDismiss = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismiss()
                            onBottomSheetDismiss()
                        }
                    }
                },
                onViewDetails = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismiss()
                            onNavigate("test_details")
                        }
                    }
                },
                description,
                testCases
            )
        }

        else -> {}
    }
}






