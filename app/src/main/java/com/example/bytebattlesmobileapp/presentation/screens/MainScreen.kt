package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.bytebattlesmobileapp.presentation.components.CardNewsOrTask
import com.example.bytebattlesmobileapp.presentation.components.UserHeader
import com.example.bytebattlesmobileapp.presentation.components.UserTopCard
import com.example.bytebattlesmobileapp.presentation.viewmodel.LeaderboardViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToMenu: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBattle: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToNewStorm: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val leaderboardUiState by leaderboardViewModel.uiState.collectAsStateWithLifecycle()
    val isLeaderboardLoading by leaderboardViewModel.isLoading.collectAsStateWithLifecycle()

    // Загружаем лидерборд при запуске
    LaunchedEffect(Unit) {
        leaderboardViewModel.loadLeaderboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        // UserHeader с реальным именем пользователя
        when (uiState) {
            is ProfileViewModel.ProfileUiState.Success -> {
                val data = (uiState as ProfileViewModel.ProfileUiState.Success).data
                UserHeader(
                    name = data.profile.userName,
                    painter = painterResource(R.drawable.userprofile),
                    showIcon = true,
                    {onNavigateToMenu(data.profile.userName)}
                )
            }
            else -> {
                UserHeader(
                    name = "Loading...",
                    painter = painterResource(R.drawable.userprofile),
                    showIcon = true,
                    {onNavigateToMenu("")}
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Text(
                        "Топ игроков",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Отображение лидерборда
                    when (leaderboardUiState) {
                        is LeaderboardViewModel.LeaderboardUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF5EC2C3))
                            }
                        }

                        is LeaderboardViewModel.LeaderboardUiState.Empty -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Лидерборд пуст",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        is LeaderboardViewModel.LeaderboardUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Ошибка загрузки",
                                        color = Color.Red,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Попробуйте позже",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        is LeaderboardViewModel.LeaderboardUiState.Success -> {
                            val leaders = (leaderboardUiState as LeaderboardViewModel.LeaderboardUiState.Success).leaders

                            if (leaders.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Нет данных",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            } else {
                                // Показываем топ 5 игроков
                                leaders.take(5).forEachIndexed { index, leader ->
                                    UserTopCard(
                                        nickUser = leader.userName,
                                        painter = painterResource(R.drawable.icon_man),
                                        point = leader.totalExperience.toString(),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Новости
                    Text(
                        "Новости",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Карточка новости 1
                    CardNewsOrTask(
                        nameOfNews = "Новый турнир 'Зимний кодинг'",
                        date = "15 декабря 2023",
                        description = "Приглашаем всех принять участие в зимнем турнире по программированию. Призы: курс по алгоритмам, мерч и денежные призы.",
                        onNavigateToTaskInfo = {
                            // Навигация к деталям новости
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Карточка новости 2
                    CardNewsOrTask(
                        nameOfNews = "Обновление платформы",
                        date = "10 декабря 2023",
                        description = "Добавлены новые языки программирования: Rust и Go. Улучшена система проверки решений.",
                        onNavigateToTaskInfo = {
                            // Навигация к деталям новости
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Карточка новости 3
                    CardNewsOrTask(
                        nameOfNews = "Конкурс на лучшую задачу",
                        date = "5 декабря 2023",
                        description = "Присылайте свои задачи до 25 декабря. Авторы лучших задач получат премиум-аккаунт на год.",
                        onNavigateToTaskInfo = {
                            // Навигация к деталям новости
                        }
                    )

                }
            }
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        onNavigateToMenu = {},
        onNavigateToProfile = {},
        onNavigateToBattle = {},
        onNavigateToStatistics = {},
        onNavigateToNewStorm = {}
    )
}