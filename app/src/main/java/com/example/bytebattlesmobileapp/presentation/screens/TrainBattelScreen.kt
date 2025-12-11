package com.example.bytebattlesmobileapp.presentation.screens

import CodeEditor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyUiState
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
    val messages by battleLobbyViewModel.messages.collectAsStateWithLifecycle()
    val uiState by battleLobbyViewModel.uiState.collectAsStateWithLifecycle()
    val taskState by viewModel.taskState.collectAsStateWithLifecycle()

    // Состояние для результата битвы
    var battleResult by remember { mutableStateOf<BattleResult?>(null) }

    // Состояние для отображения уведомлений
    var showNotifications by remember { mutableStateOf(false) }
    val visibleNotifications = remember { mutableStateListOf<IncomingBattleMessage>() }

    // Загружаем задачу
    LaunchedEffect(taskId) {
        if (taskId.isNotEmpty()) {
            println("TrainBattleScreen: Loading task with id: $taskId")
            viewModel.getTaskById(UUID.fromString(taskId))
        }
    }

    // Обработка новых сообщений для уведомлений
    LaunchedEffect(messages) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null) {
            // Добавляем сообщение в список видимых уведомлений
            visibleNotifications.add(lastMessage)

            // Автоматически скрываем уведомление через 5 секунд
            scope.launch {
                delay(5000)
                visibleNotifications.remove(lastMessage)
            }


        }
    }
    val handleBattleMessage: (IncomingBattleMessage, String?) -> Unit = { message, playerId ->
        when (message) {
            is IncomingBattleMessage.BattleWon -> {
                val isCurrentPlayerWinner = message.winnerId == playerId
                battleResult = BattleResult(
                    isWinner = isCurrentPlayerWinner,
                    taskTitle = message.taskTitle,
                    message = message.message
                )
            }
            is IncomingBattleMessage.BattleLost -> {
                battleResult = BattleResult(
                    isWinner = false,
                    taskTitle = message.taskTitle,
                    message = message.message
                )
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
    LaunchedEffect(messages) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null) {
            // Добавляем сообщение в список видимых уведомлений
            visibleNotifications.add(lastMessage)

            // Обработка специальных сообщений для результата битвы
            handleBattleMessage(lastMessage, battleLobbyViewModel.getPlayerId())

            // Автоматически скрываем уведомление через 5 секунд
            scope.launch {
                delay(5000)
                visibleNotifications.remove(lastMessage)
            }
        }
    }
    // Функция для обработки боевых сообщений


    // Отслеживаем состояние задачи
    LaunchedEffect(taskState) {
        when (taskState) {
            is TaskViewModel.TaskDetailState.Success -> {
                val task = (taskState as TaskViewModel.TaskDetailState.Success).task
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

    // Обработка закрытия результата битвы
    LaunchedEffect(battleResult) {
        battleResult?.let {
            delay(10000)
            battleResult = null
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

        // Панель уведомлений
        NotificationPanel(
            notifications = visibleNotifications,
            onToggleVisibility = { showNotifications = !showNotifications },
            isVisible = showNotifications,
            onClearNotifications = { visibleNotifications.clear() }
        )

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
            onNavigateMain = onNavigateMain,
            onDismiss = { modalState = ModalState.NONE },
            onNavigate = { modalState = ModalState.NONE },
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

@Composable
fun NotificationPanel(
    notifications: List<IncomingBattleMessage>,
    onToggleVisibility: () -> Unit,
    isVisible: Boolean,
    onClearNotifications: () -> Unit
) {
    val unreadCount = notifications.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 70.dp, end = 8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        // Кнопка/иконка уведомлений
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (unreadCount > 0) Color(0xFFFF5722) else Color(0xFF3A4659),
                    shape = CircleShape
                )
                .clickable { onToggleVisibility() },
            contentAlignment = Alignment.Center
        ) {
            // Иконка колокольчика
            Icon(
                painter = painterResource(id = R.drawable.menu_notification), // Добавьте свою иконку
                contentDescription = "Уведомления",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            // Бейдж с количеством
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Red, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Панель с уведомлениями
        if (isVisible && notifications.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 56.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3A4659)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Заголовок
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Уведомления ($unreadCount)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.mune_exit),
                            contentDescription = "Очистить все",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onClearNotifications() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Список уведомлений
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications.reversed()) { message ->
                            NotificationItem(message = message)
                        }
                    }
                }
            }
        }
    }
}
data class NotificationData(
    val iconRes: Int,
    val title: String,
    val content: String,
    val color: Color
)
@Composable
fun NotificationItem(message: IncomingBattleMessage) {
    val notificationData: NotificationData = when (message) {
        is IncomingBattleMessage.PlayerJoined -> {
            NotificationData(
                R.drawable.man,
                "Игрок присоединился",
                "Игрок ${message.playerId.takeLast(4)} присоединился к битве",
                Color(0xFF4CAF50)
            )
        }
        is IncomingBattleMessage.PlayerLeft -> {
            NotificationData(
                R.drawable.man,
                "Игрок вышел",
                "Игрок покинул битву",
                Color(0xFFF44336)
            )
        }
        is IncomingBattleMessage.PlayerReadyChanged -> {
            val status = if (message.isReady) "готов" else "не готов"
            NotificationData(
                R.drawable.man,
                "Изменение готовности",
                "Игрок ${message.playerId.takeLast(4)}: $status",
                if (message.isReady) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }
        is IncomingBattleMessage.CodeSubmittedByPlayer -> {
            NotificationData(
                R.drawable.man,
                "Решение отправлено",
                "Игрок ${message.playerId.takeLast(4)} отправил решение",
                Color(0xFF2196F3)
            )
        }
        is IncomingBattleMessage.GameStarted -> {
            NotificationData(
                R.drawable.man,
                "Битва началась!",
                "Задача: ${message.taskTitle}, время: ${message.duration}с",
                Color(0xFF9C27B0)
            )
        }
        is IncomingBattleMessage.GameCanStart -> {
            NotificationData(
                R.drawable.man,
                "Можно начинать",
                "Все игроки готовы. Обратный отсчет: ${message.countdown}с",
                Color(0xFFFF9800)
            )
        }
        is IncomingBattleMessage.CodeResult -> {
            val statusText = when (message.result.status) {
                "success" -> "✓ Решение принято"
                "wrong_answer" -> "✗ Неправильный ответ"
                "compile_error" -> "⚠ Ошибка компиляции"
                "runtime_error" -> "⚠ Ошибка выполнения"
                "time_limit_exceeded" -> "⏱ Превышено время"
                else -> message.result.status
            }
            NotificationData(
                R.drawable.man,
                "Результат проверки",
                "$statusText (${message.result.passedTests}/${message.result.totalTests})",
                if (message.result.status == "success") Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
        else -> {
            NotificationData(
                R.drawable.man,
                "Уведомление",
                message.toString(),
                Color(0xFF757575)
            )
        }
    }

    // Теперь можно деструктурировать notificationData
    val (iconRes, title, content, color) = notificationData

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C3646)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Текст
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = content,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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






