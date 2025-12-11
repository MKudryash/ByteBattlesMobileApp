package com.example.bytebattlesmobileapp.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.AchievementsGrid
import com.example.bytebattlesmobileapp.presentation.components.CardProfileStatistic
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.components.RowProfileStatisticPoint
import com.example.bytebattlesmobileapp.presentation.components.RowStatisticItem
import com.example.bytebattlesmobileapp.presentation.components.StatisticPoint
import com.example.bytebattlesmobileapp.presentation.components.UserHeader
import com.example.bytebattlesmobileapp.presentation.components.UserTopCard
import com.example.bytebattlesmobileapp.presentation.viewmodel.LeaderboardViewModel
import com.example.bytebattlesmobileapp.presentation.viewmodel.ProfileViewModel
import kotlin.math.round


@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val activitiesUiState by viewModel.uiStateActivities.collectAsStateWithLifecycle()
    val achievementUiState by viewModel.uiStateAchievement.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var points: Int? by remember { mutableStateOf(0) }
    var loses: Int? by remember { mutableStateOf(0) }
    var wins: Int? by remember { mutableStateOf(0) }
    var maxStreak: Int? by remember { mutableStateOf(0) }
    var winRate: Double? by remember { mutableStateOf(0.0) }

    // Заполняем поля при загрузке данных
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Success) {
            val data = (uiState as ProfileViewModel.ProfileUiState.Success).data
            Log.d("USER", data.stats.toString())
            username = data.profile.userName!!
            points = data.stats?.totalExperience
            wins = data.stats?.wins
            loses = data.stats?.losses
            maxStreak = data.stats?.maxStreak
            winRate = data.stats?.winRate
        }
    }

    LaunchedEffect(activitiesUiState) {
        viewModel.loadActivities()
    }

    // Обработка ошибок
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Показать Snackbar
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        UserHeader(
            username,
            painter = painterResource(R.drawable.userprofile),
            true,
            onNavigateBack
        )

        Spacer(Modifier.height(25.dp))

        // Боевой профиль
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Боевой профиль",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                )

                Text(
                    "$points очков ",
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardProfileStatistic("✅ Победы", "$wins (${"%.2f".format(winRate) }%)")
                CardProfileStatistic("⛔Поражения", "${loses}")
                CardProfileStatistic("\uD83C\uDFF9 Лучшая серия", "${maxStreak} побед")
            }

            Spacer(Modifier.height(25.dp))
        }

        // История очков
        Text(
            "История очков",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
        )

        Spacer(Modifier.height(15.dp))

        // Состояния загрузки истории очков
        when (activitiesUiState) {
            ProfileViewModel.ActivitiesUIState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Список активностей пуст",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            is ProfileViewModel.ActivitiesUIState.Error -> {
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

            ProfileViewModel.ActivitiesUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5EC2C3))
                }
            }

            is ProfileViewModel.ActivitiesUIState.Success -> {
                val activities =
                    (activitiesUiState as ProfileViewModel.ActivitiesUIState.Success).data

                if (activities.isEmpty()) {
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
                    // Используем LazyColumn только для списка активностей
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 5.dp
                                    ), // такой же padding как у элементов
                            ) {
                                val style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                                )
                                Text(
                                    modifier = Modifier.weight(0.2f), // СОВПАДАЕТ с первым столбцом
                                    text = "Дата",
                                    style = style

                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    modifier = Modifier.weight(0.4f), // СОВПАДАЕТ со вторым столбцом
                                    text = "Задача",
                                    style = style
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    modifier = Modifier.weight(0.15f), // СОВПАДАЕТ с третьим столбцом
                                    text = "Тип",
                                    style = style
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    modifier = Modifier.weight(0.25f),
                                    text = "Начислено",
                                    style = style
                                )
                            }
                        }

                        items(activities.take(10)) { activity ->
                            RowStatisticItem(point = activity)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        // Достижения
        Text(
            "Достижения",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
        )

        Spacer(Modifier.height(15.dp))

        // Сетка достижений - исправленная версия
        /*val achievements = listOf(
            Achievement(R.drawable.firstblood, "Первая кровь"),
            Achievement(R.drawable.firstblood, "Быстрый ученик"),
            Achievement(R.drawable.firstblood, "Решатель проблем"),
            Achievement(R.drawable.firstblood, "Мастер кода"),
            Achievement(R.drawable.firstblood, "Командный игрок"),
            Achievement(R.drawable.firstblood, "Перфекционист"),
            Achievement(R.drawable.firstblood, "Инноватор"),
            Achievement(R.drawable.firstblood, "Легенда")
        )*/
        when (achievementUiState) {
            ProfileViewModel.AchievementsUIState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Список наград пуст",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            is ProfileViewModel.AchievementsUIState.Error -> {
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

            ProfileViewModel.AchievementsUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5EC2C3))
                }
            }

            is ProfileViewModel.AchievementsUIState.Success -> {
                val achievements =
                    (achievementUiState as ProfileViewModel.AchievementsUIState.Success).data

                if (achievements.isEmpty()) {
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

                    AchievementsGrid(
                        achievements = achievements,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 400.dp)
                    )

                }
            }
        }
    }


    Spacer(Modifier.height(25.dp))
}


@Preview
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen({})
}
