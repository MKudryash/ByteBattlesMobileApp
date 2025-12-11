package com.example.bytebattlesmobileapp.presentation.screens

import CodeEditor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.domain.model.TestCase
import com.example.bytebattlesmobileapp.domain.model.TestResult
import com.example.bytebattlesmobileapp.presentation.components.CardTest
import com.example.bytebattlesmobileapp.presentation.components.CircleButton
import com.example.bytebattlesmobileapp.presentation.components.CustomInfoDialog
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.viewmodel.Notification
import com.example.bytebattlesmobileapp.presentation.viewmodel.NotificationType
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.emptyList

// Enum для управления всеми состояниями модальных окон
enum class ModalState {
    NONE,
    FINISH_TASK,
    SUBMIT_CODE,
    TESTS_BOTTOM_SHEET,
    TEST_DETAILS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainScreen(
    onNavigateMain:()->Unit,
    onNavigateBack: () -> Unit,
    taskId: String,
    viewModel: TaskViewModel = hiltViewModel()
) {
    Log.d("Train", "TaskId: $taskId")

    val taskState by viewModel.taskState.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    var nameTask by remember { mutableStateOf("Name of Task") }
    var initialCode by remember { mutableStateOf("// Write your code here") }
// Используем nullable и инициализируем только один раз
    var currentCode by remember { mutableStateOf<String?>(null) }
    var languageId by remember { mutableStateOf("") }
    var isCodeInitialized by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var testCases:List<TestCase> by remember { mutableStateOf(emptyList()) }


    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    // Загружаем задачу только один раз при инициализации
    LaunchedEffect(Unit) {
        if (taskId.isNotEmpty()) {
            try {
                viewModel.getTaskById(UUID.fromString(taskId))
            } catch (e: IllegalArgumentException) {
                Log.e("TrainScreen", "Invalid UUID: $taskId", e)
            }
        }
    }

    // Обновляем данные при изменении состояния
    LaunchedEffect(taskState) {
        when (val state = taskState) {
            is TaskViewModel.TaskDetailState.Success -> {
                nameTask = state.task.title
                initialCode = state.task.patternFunction
                languageId = state.task.language?.id ?: ""
                description = state.task.description
                testCases = state.task.testCases

                // Инициализируем currentCode только один раз
                if (!isCodeInitialized && currentCode == null) {
                    currentCode = state.task.patternFunction
                    isCodeInitialized = true
                    Log.d("TRAIN", "Initialized code: ${state.task.patternFunction}")
                }
                Log.d("TRAIN", "Task loaded: ${state.task.title}")
            }

            is TaskViewModel.TaskDetailState.Error -> {
                Log.e("TrainScreen", "Error loading task")
            }

            TaskViewModel.TaskDetailState.Loading -> {
                // Состояние загрузки - ничего не делаем
            }
        }
    }

    var modalState by remember { mutableStateOf(ModalState.NONE) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    val onNavigate = { destination: String ->
        modalState = ModalState.NONE
    }

    // Функция для отправки решения
    val onSubmitSolution = {
        if (currentCode?.isNotEmpty() == true && languageId.isNotEmpty() && taskId.isNotEmpty()) {
            viewModel.submitSolution(currentCode!!, languageId, taskId)
            modalState = ModalState.NONE
        } else {
            Log.e("TrainScreen", "Cannot submit: missing data")
        }
    }

    // Обработка результата отправки
    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is TaskViewModel.SubmitSolutionState.Success -> {
                Log.d("TrainScreen", "Solution submitted successfully")
                Log.d("Submit Success", state.solution.successRate.toString())
                delay(2000)
                viewModel.clearSubmitState()
            }

            is TaskViewModel.SubmitSolutionState.Error -> {
                Log.e("TrainScreen", "Submit error: ${state.error}")
                delay(3000)
                viewModel.clearSubmitState()
            }

            else -> {

            }
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
                onNavigateBack = { onNavigateBack() },
                textHeader = when {
                    submitState is TaskViewModel.SubmitSolutionState.Loading -> "$nameTask (Отправка...)"
                    submitState is TaskViewModel.SubmitSolutionState.Success -> "$nameTask ✓"
                    submitState is TaskViewModel.SubmitSolutionState.Error -> "$nameTask ✗"
                    else -> when (taskState) {
                        TaskViewModel.TaskDetailState.Loading -> "Загрузка..."
                        is TaskViewModel.TaskDetailState.Success -> nameTask
                        is TaskViewModel.TaskDetailState.Error -> "Ошибка"
                    }
                }
            )

            // Показываем индикатор загрузки при отправке
            if (submitState is TaskViewModel.SubmitSolutionState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Отправка решения...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Обрабатываем разные состояния загрузки
            when (val state = taskState) {
                TaskViewModel.TaskDetailState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                is TaskViewModel.TaskDetailState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Не удалось загрузить задачу",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Error",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (taskId.isNotEmpty()) {
                                        try {
                                            viewModel.getTaskById(UUID.fromString(taskId))
                                        } catch (e: Exception) {
                                            Log.e("TrainScreen", "Retry error", e)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF53C2C3)
                                )
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                is TaskViewModel.TaskDetailState.Success -> {
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


                        // Проверяем, что currentCode не null
                        currentCode?.let { code ->
                            CodeEditor(
                                language = getCodeLang(state.task.language!!.title),
                                initialCode = code,
                                onCodeChange = { newCode ->
                                    currentCode = newCode
                                    Log.d("TrainScreen", "Code updated: ${newCode.length} chars")
                                }
                            )
                        } ?: run {
                            // Показываем индикатор загрузки, пока код не загружен
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
            }
        }

        // Показываем кнопки действий только при успешной загрузке и не во время отправки
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
        NotificationOverlay(
            notifications = notifications,
            onDismissNotification = { id ->
                viewModel.dismissNotification(id)
            }
        )
        // Управление всеми модальными окнами
        ModalWindowsManager(
            modalState = modalState,
            onDismiss = { modalState = ModalState.NONE },
            onNavigate = onNavigate,
            onNavigateMain =onNavigateMain,
            bottomSheetState = bottomSheetState,
            scope = scope,
            onBottomSheetDismiss = { showBottomSheet = false },
            onSubmitSolution = { onSubmitSolution() },
            testCases,
            description
        )
    }
}
@Composable
fun NotificationOverlay(
    notifications: List<Notification>,
    onDismissNotification: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp), // Отступ от хедера
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            notifications.forEach { notification ->
                NotificationItem(
                    notification = notification,
                    onDismiss = { onDismissNotification(notification.id) }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (notification.type) {
        NotificationType.SUCCESS -> Color(0xFF4CAF50)
        NotificationType.ERROR -> Color(0xFFF44336)
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.INFO -> Color(0xFF2196F3)
    }

    // Автоматическое скрытие через 5 секунд
    LaunchedEffect(notification.id) {
        kotlinx.coroutines.delay(5000)
        onDismiss()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка
            Icon(
                painter = painterResource(
                    when (notification.type) {
                        NotificationType.SUCCESS -> R.drawable.check
                        NotificationType.ERROR -> R.drawable.error_ic
                        NotificationType.WARNING -> R.drawable.error
                        NotificationType.INFO -> R.drawable.inform
                    }
                ),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Текст
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = notification.message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Кнопка закрытия
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }
        }
    }
}
// Обновите ModalWindowsManager, чтобы принимать onSubmitSolution
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalWindowsManager(
    modalState: ModalState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateMain: () -> Unit,
    bottomSheetState: SheetState,
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
                onNavigateToInfo = {onNavigateMain() },
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


// Вынесенные компоненты для лучшей читаемости

@Composable
 fun BottomActionButtons(
    onFinishClick: () -> Unit,
    onInfoClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerText()

            CircleButton(
                painterResource(R.drawable.close),
                onClick = onFinishClick,
                color = Color(0xFFF44336)
            )

            CircleButton(
                painterResource(R.drawable.inform),
                onClick = onInfoClick,
                color = Color(0xFFFF9800)
            )

            CircleButton(
                painterResource(R.drawable.succes),
                onClick = onSubmitClick,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun TimerText() {
    Text(
        modifier = Modifier.padding(horizontal = 10.dp),
        text = "00:30",
        color = Color.White,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun TestsBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit,
    description: String,
    testCases: List<TestCase>
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF2C3646),
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            DragHandle()
        }
    ) {
        TrainBottomSheetContent(
            description = description,
            testCases
        )
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(
                    color = Color(0xFF53C2C3).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

@Composable
fun TrainBottomSheetContent(
    description: String,
    testCases: List<TestCase>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        SectionTitle("Описание")
        SectionText(description)

        Spacer(Modifier.height(25.dp))

        SectionTitle("Пример теста")

        LazyColumn {
            items(testCases) {
                if (!it.isExample) {
                    CardTest(it.input, it.output)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
    )
}

@Composable
private fun SectionText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
    )
}


@Composable
fun getCodeLang(languageTitle: String): CodeLang {
    Log.d("getCodeLang", languageTitle)
    return when (languageTitle) {
        "Java" -> CodeLang.Java
        "Python" -> CodeLang.Python
        "C" -> CodeLang.C
        "C#" -> CodeLang.CSharp
        else -> CodeLang.Markdown
    }
}

@Preview
@Composable
fun TrainScreenPreview() {
    TrainScreen({}, {},"")
}