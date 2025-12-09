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
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.emptyList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainBattleScreen(
    onNavigateBack: () -> Unit,
    taskId: String,
    viewModel: TaskViewModel = hiltViewModel()
) {
    Log.d("Train", "TaskId: $taskId")


    var nameTask by remember { mutableStateOf("Name of Task") }
    var initialCode by remember { mutableStateOf("// Write your code here") }
// Используем nullable и инициализируем только один раз
    var currentCode by remember { mutableStateOf<String?>(null) }
    var languageId by remember { mutableStateOf("") }
    var isCodeInitialized by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var testCases:List<TestCase> by remember { mutableStateOf(emptyList()) }




    var modalState by remember { mutableStateOf(ModalState.NONE) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    val onNavigate = { destination: String ->
        modalState = ModalState.NONE
    }
    val taskState by viewModel.taskState.collectAsStateWithLifecycle()

    // Загружаем задачу при получении taskId
    LaunchedEffect(taskId) {
        if (taskId.isNotEmpty()) {
            println("TrainBattleScreen: Loading task with id: $taskId")
            viewModel.getTaskById(UUID.fromString(taskId))
        }
    }

    // Отслеживаем состояние задачи
    LaunchedEffect(taskState) {
        when (taskState) {
            is TaskViewModel.TaskDetailState.Success -> {
                val task = (taskState as TaskViewModel.TaskDetailState.Success).task
                println("TrainBattleScreen: Task loaded: ${task.title}")
                // Здесь можно обновить UI с данными задачи
            }
            is TaskViewModel.TaskDetailState.Loading -> {
                println("TrainBattleScreen: Loading task...")
            }
            is TaskViewModel.TaskDetailState.Error -> {
                val error = (taskState as TaskViewModel.TaskState.Error).message
                println("TrainBattleScreen: Error loading task: $error")
            }
            else -> {}
        }
    }
    // Функция для отправки решения
    val onSubmitSolution = {

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
                textHeader = when (taskState) {
                    is TaskViewModel.TaskDetailState.Success ->
                        (taskState as TaskViewModel.TaskDetailState.Success).task.title
                    else -> "Битва"
                }
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


                        // Проверяем, что currentCode не null
                        currentCode?.let { code ->
                            CodeEditor(
                                language = getCodeLang("Charp"),
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



        // Управление всеми модальными окнами
        ModalWindowsManager(
            modalState = modalState,
            onDismiss = { modalState = ModalState.NONE },
            onNavigate = onNavigate,
            bottomSheetState = bottomSheetState,
            scope = scope,
            onBottomSheetDismiss = { showBottomSheet = false },
            onSubmitSolution = { onSubmitSolution },
            testCases,
            description
        )
    }
}

// Обновите ModalWindowsManager, чтобы принимать onSubmitSolution
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalWindowsManager(
    modalState: ModalState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
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
                onNavigateToInfo = { onNavigate("task_details") },
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






