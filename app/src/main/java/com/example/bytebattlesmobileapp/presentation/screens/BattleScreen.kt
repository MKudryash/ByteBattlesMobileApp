package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CardLanguage
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleRoomParams
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BattleScreen(
    onNavigateBack: () -> Unit,
    onNavigateTrain: (String) -> Unit,
    viewModel: BattleLobbyViewModel ,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    var selectedTypeBattle by remember { mutableStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf(0) }
    val selectedLanguageId by taskViewModel.selectedLanguageId.collectAsStateWithLifecycle()
    val languagesState by taskViewModel.languageState.collectAsStateWithLifecycle()
    val languages by taskViewModel.languages.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    // Отслеживаем создание комнаты
    LaunchedEffect(uiState.roomId, uiState.isConnected) {
        println("BattleScreen: UI State - roomId=${uiState.roomId}, isConnected=${uiState.isConnected}, battleState=${uiState.battleState}")

        // Если комната создана и мы подключены, переходим в лобби
        if (uiState.roomId.isNotEmpty() && uiState.isConnected) {
            println("BattleScreen: Room created and connected! Navigating to lobby...")
            delay(1000) // Даем время на отображение изменений
        }
    }

    // Отслеживаем ошибки
    LaunchedEffect(uiState.connectionError) {
        uiState.connectionError?.let { error ->
            if (error.isNotBlank() && error != "null") {
                println("BattleScreen: Connection error: $error")
            }
        }
    }

    // Отладка сообщений
    LaunchedEffect(messages) {
        messages.lastOrNull()?.let { message ->
            println("BattleScreen: Last message: $message")
        }
    }

    // Списки параметров
    val difficulties = listOf(
        TypeOf("Easy", R.drawable.easy, 1),
        TypeOf("Middle", R.drawable.middle, 2),
        TypeOf("Hard", R.drawable.hard, 3)
    )

    val battleTypes = listOf(
        TypeOf("1 vs 1", R.drawable.one_vs_one, 1),
        TypeOf("Командный", R.drawable.command, 2),
        TypeOf("Турнир", R.drawable.champ, 3)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Header({ onNavigateBack() }, "Battle")
        Spacer(Modifier.height(20.dp))

        LazyColumn {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    // Статус подключения
                    ConnectionStatusCard(uiState)

                    // Язык программирования
                    Text(
                        "Язык программирования",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )

                    Spacer(Modifier.height(12.dp))

                    when (languagesState) {
                        is TaskViewModel.LanguageState.Loading -> {
                            LoadingView()
                        }

                        is TaskViewModel.LanguageState.Empty -> {
                            EmptyView("Языки не найдены")
                        }

                        is TaskViewModel.LanguageState.Error -> {
                            ErrorView((languagesState as TaskViewModel.LanguageState.Error).message)
                        }

                        is TaskViewModel.LanguageState.Success -> {
                            LanguageSelectionView(
                                languages = languages,
                                selectedLanguageId = selectedLanguageId,
                                onLanguageSelected = { language ->
                                    taskViewModel.selectLanguage(language.id)
                                }
                            )
                        }
                    }

                    // Уровень сложности
                    DifficultySelectionView(
                        difficulties = difficulties,
                        selectedDifficulty = selectedDifficulty,
                        onDifficultySelected = { selectedDifficulty = it }
                    )

                    // Тип битвы
                    BattleTypeSelectionView(
                        battleTypes = battleTypes,
                        selectedTypeBattle = selectedTypeBattle,
                        onBattleTypeSelected = { selectedTypeBattle = it }
                    )

                    // Кнопка "Начать бой"
                    StartBattleButton(
                        uiState = uiState,
                        selectedLanguageId = selectedLanguageId,
                        languages = languages,
                        selectedDifficulty = selectedDifficulty,
                        difficulties = difficulties,
                        onStartBattle = {
                            val selectedLang = languages.find { it.id == selectedLanguageId }
                            val selectedDiff = difficulties.find { it.id == selectedDifficulty }

                            if (selectedLang != null && selectedDiff != null) {
                                // Устанавливаем параметры комнаты
                                viewModel.roomParams = BattleRoomParams(
                                    roomName = "${selectedLang.title} ${selectedDiff.name} Battle",
                                    languageId = selectedLang.id.trim(),
                                    difficulty = selectedDiff.name
                                )

                                println("=== STARTING BATTLE ===")
                                println("Room params: ${viewModel.roomParams}")
                                println("Current state: roomId=${uiState.roomId}, isConnected=${uiState.isConnected}")

                                scope.launch {
                                    if (!uiState.isConnected) {
                                        println("Connecting to WebSocket...")
                                        viewModel.connect("")

                                        // Ждем подключения
                                        var attempts = 0
                                        while (attempts < 10 && !uiState.isConnected) {
                                            delay(500)
                                            attempts++
                                            println("Waiting for connection... attempt $attempts")
                                        }

                                        println("Failed to connect after 5 seconds")
                                    } else {
                                        println("Already connected. Creating room...")
                                        viewModel.createRoom()
                                    }
                                }
                            } else {
                                println("ERROR: Language or difficulty not selected!")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionStatusCard(uiState: com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyUiState) {
    if (uiState.isConnected || uiState.isLoading) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    uiState.isConnected -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    uiState.isLoading -> Color(0xFFFF9800).copy(alpha = 0.2f)
                    else -> Color(0xFFF44336).copy(alpha = 0.2f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            when {
                                uiState.isConnected -> Color.Green
                                uiState.isLoading -> Color.Yellow
                                else -> Color.Red
                            },
                            CircleShape
                        )
                )
                Text(
                    text = when {
                        uiState.isConnected -> "Подключено к серверу"
                        uiState.isLoading -> "Подключение..."
                        else -> "Не подключено"
                    },
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LanguageSelectionView(
    languages: List<com.example.bytebattlesmobileapp.domain.model.Language>,
    selectedLanguageId: String?,
    onLanguageSelected: (com.example.bytebattlesmobileapp.domain.model.Language) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(languages) { index, language ->
            CardLanguage(
                painter = painterResource(getLanguageIcon(language.title)),
                nameLanguage = language.title,
                selected = selectedLanguageId == language.id,
                onClick = { onLanguageSelected(language) }
            )
        }
    }
}

@Composable
private fun DifficultySelectionView(
    difficulties: List<TypeOf>,
    selectedDifficulty: Int,
    onDifficultySelected: (Int) -> Unit
) {
    Text(
        "Уровень сложности",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
    )

    Spacer(Modifier.height(12.dp))
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(difficulties) { difficulty ->
            CardLanguage(
                painter = painterResource(difficulty.iconRes),
                nameLanguage = difficulty.name,
                selected = selectedDifficulty == difficulty.id,
                onClick = { onDifficultySelected(difficulty.id) }
            )
        }
    }
}

@Composable
private fun BattleTypeSelectionView(
    battleTypes: List<TypeOf>,
    selectedTypeBattle: Int,
    onBattleTypeSelected: (Int) -> Unit
) {
    Spacer(Modifier.height(10.dp))
    Text(
        "Тип битвы",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
    )

    Spacer(Modifier.height(12.dp))
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(battleTypes) { battleType ->
            CardLanguage(
                painter = painterResource(battleType.iconRes),
                nameLanguage = battleType.name,
                selected = selectedTypeBattle == battleType.id,
                onClick = { onBattleTypeSelected(battleType.id) }
            )
        }
    }
}

@Composable
private fun StartBattleButton(
    uiState: com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyUiState,
    selectedLanguageId: String?,
    languages: List<com.example.bytebattlesmobileapp.domain.model.Language>,
    selectedDifficulty: Int,
    difficulties: List<TypeOf>,
    onStartBattle: () -> Unit
) {
    Spacer(Modifier.height(80.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isEnabled = !uiState.isLoading &&
                selectedLanguageId != null &&
                selectedDifficulty > 0 &&
                uiState.roomId.isEmpty() // Только если комната еще не создана

        val buttonText = when {
            uiState.isLoading -> "ПОДКЛЮЧЕНИЕ..."
            uiState.roomId.isNotEmpty() -> "КОМНАТА СОЗДАНА"
            uiState.isConnected -> "СОЗДАТЬ КОМНАТУ"
            else -> "НАЧАТЬ БОЙ"
        }

        val buttonColor = when {
            uiState.isLoading -> Color(0xFF757575)
            uiState.roomId.isNotEmpty() -> Color(0xFF4CAF50)
            uiState.isConnected ->Color(0xFF5EC2C3)
            else -> Color(0xFF5EC2C3)
        }

        ActionButton(
            text = buttonText.uppercase(),
            onClick = onStartBattle,
            color = buttonColor,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp),
            enabled = isEnabled
        )

        // Отображение статуса
        if (uiState.isLoading) {
            Text(
                text = "Подключение к серверу...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (uiState.roomId.isNotEmpty()) {
            Text(
                text = "Переход в лобби...",
                color = Color(0xFF4CAF50),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (!uiState.isConnected) {
            Text(
                text = "Нажмите, чтобы подключиться и создать комнату",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.White)
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = "Ошибка",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.White)
        }
    }
}



data class TypeOf(
    val name: String,
    val iconRes: Int,
    val id: Int
)