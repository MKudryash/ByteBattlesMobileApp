package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.CardLanguage
import com.example.bytebattlesmobileapp.presentation.components.CardNewsOrTask
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.UserHeader
import com.example.bytebattlesmobileapp.presentation.viewmodel.ProfileViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    onNavigateToTaskInfo: (String) -> Unit,
    onNavigateSideMenu: () -> Unit,
    taskViewModel: TaskViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userName: String?
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val tasksState by taskViewModel.tasksState.collectAsStateWithLifecycle()
    val languagesState by taskViewModel.languageState.collectAsStateWithLifecycle()
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val languages by taskViewModel.languages.collectAsStateWithLifecycle()
    val selectedLanguageId by taskViewModel.selectedLanguageId.collectAsStateWithLifecycle()

    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    var username by remember {  mutableStateOf("")}
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Success) {
            val data = (uiState as ProfileViewModel.ProfileUiState.Success).data
            username = data.profile.userName!!
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {

        UserHeader(username, painter = null, showIcon = true,onNavigateSideMenu)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp)
        ) {
            item {
                Column {
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
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is TaskViewModel.LanguageState.Empty -> {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Языки не найдены",
                                    color = Color.White
                                )
                            }
                        }

                        is TaskViewModel.LanguageState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
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
                                    Text(
                                        text =  "Что-то пошло не так попробуйте позже",
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        is TaskViewModel.LanguageState.Success -> {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                // Добавляем опцию "Все языки"
                                item {
                                    CardLanguage(
                                        painter = painterResource(R.drawable.icon_dark), // Добавьте иконку для всех языков
                                        nameLanguage = "Все",
                                        selected = selectedLanguageId == null,
                                        onClick = {
                                            selectedIndex = -1
                                            taskViewModel.selectLanguage(null)
                                        }
                                    )
                                }

                                itemsIndexed(languages) { index, language ->
                                    CardLanguage(
                                        painter = painterResource(getLanguageIcon(language.title)), // Нужна функция для получения иконки
                                        nameLanguage = language.title,
                                        selected = selectedLanguageId == language.id,
                                        onClick = {
                                            selectedIndex = index
                                            print(language.id)
                                            taskViewModel.selectLanguage(language.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Задачи",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )


                    Spacer(Modifier.height(8.dp))
                }
            }

            // Отображаем задачи или состояние загрузки
            when (tasksState) {
                is TaskViewModel.TaskState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is TaskViewModel.TaskState.Empty -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Задачи не найдены",
                                    color = Color.White
                                )
                                if (selectedLanguageId != null) {
                                    Text(
                                        text = "Попробуйте выбрать другой язык",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                is TaskViewModel.TaskState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
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
                                Text(
                                    text =  "Что-то пошло не так попробуйте позже",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                is TaskViewModel.TaskState.Success -> {
                    // Добавляем задачи как отдельные items
                    items(tasks) { task ->
                        CardNewsOrTask(
                            task.title,
                            task.difficulty,
                            task.title,
                            { onNavigateToTaskInfo(task.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Функция для получения иконки языка (добавьте свою логику)
@Composable
fun getLanguageIcon(languageId: String): Int {
    return when (languageId) {
        "Java" -> R.drawable.language_java
        "Python" -> R.drawable.language_python
        "C" -> R.drawable.language_c
        "C#" -> R.drawable.csharp
        // Добавьте другие языки
        else -> R.drawable.all_language
    }
}
@Preview
@Composable
fun TaskScreenPreview() {
    TaskScreen({}, {}, userName = "")
}