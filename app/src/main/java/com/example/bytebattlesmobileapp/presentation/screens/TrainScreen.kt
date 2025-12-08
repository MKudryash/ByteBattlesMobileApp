package com.example.bytebattlesmobileapp.presentation.screens

import CodeEditor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.CardTest
import com.example.bytebattlesmobileapp.presentation.components.CircleButton
import com.example.bytebattlesmobileapp.presentation.components.CustomInfoDialog
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Enum для управления всеми состояниями модальных окон
enum class ModalState {
    NONE,
    FINISH_TASK,
    SUBMIT_CODE,
    TESTS_BOTTOM_SHEET,
    TEST_DETAILS,
    // Добавьте другие состояния по мере необходимости
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainScreen(
    onNavigateToTrainInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var nameTask by remember { mutableStateOf("Name of Task") }
    var modalState by remember { mutableStateOf(ModalState.NONE) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    // Обработчик навигации
    val onNavigate = { destination: String ->
        modalState = ModalState.NONE
        onNavigateToTrainInfo(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header({ onNavigateBack() }, nameTask)

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

                CodeEditor(language = CodeLang.CSharp)
            }
        }

        // Три круга в правом нижнем углу экрана
        BottomActionButtons(
            onFinishClick = { modalState = ModalState.FINISH_TASK },
            onInfoClick = { modalState = ModalState.TESTS_BOTTOM_SHEET },
            onSubmitClick = { modalState = ModalState.SUBMIT_CODE }
        )

        // Управление всеми модальными окнами
        ModalWindowsManager(
            modalState = modalState,
            onDismiss = { modalState = ModalState.NONE },
            onNavigate = onNavigate,
            bottomSheetState = bottomSheetState,
            scope = scope,
            onBottomSheetDismiss = { showBottomSheet = false }
        )
    }
}

// Вынесенные компоненты для лучшей читаемости

@Composable
private fun BottomActionButtons(
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
private fun ModalWindowsManager(
    modalState: ModalState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    onBottomSheetDismiss: () -> Unit
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
                onNavigateToInfo = { onNavigate("task_details") },
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
                }
            )
        }

        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestsBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit
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
            description = "sfnlkjsfklbdskfbkldfgbslgdhb",
            onClose = onDismiss,
            onRunTest = { /* Запустить тест */ },
            onViewDetails = onViewDetails
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
    onClose: () -> Unit,
    onRunTest: () -> Unit,
    onViewDetails: () -> Unit
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

        // Тестовые карточки
        repeat(3) {
            CardTest("2 3", "5")
        }

        // Кнопки действий
        ActionButtons(
            onClose = onClose,
            onRunTest = onRunTest,
            onViewDetails = onViewDetails
        )
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
private fun ActionButtons(
    onClose: () -> Unit,
    onRunTest: () -> Unit,
    onViewDetails: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onClose,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = Color.Transparent
            )
        ) {
            Text("Закрыть")
        }

        Button(
            onClick = onRunTest,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF53C2C3)
            )
        ) {
            Text("Запустить тест")
        }

        Button(
            onClick = onViewDetails,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("Подробнее")
        }
    }
}

@Preview
@Composable
fun TrainScreenPreview() {
    TrainScreen({}, {})
}