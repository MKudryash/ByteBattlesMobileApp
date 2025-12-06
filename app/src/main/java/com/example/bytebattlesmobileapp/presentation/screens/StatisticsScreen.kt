package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.bytebattlesmobileapp.presentation.components.AchievementsGrid
import com.example.bytebattlesmobileapp.presentation.components.CardProfileStatistic
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.components.RowProfileStatisticPoint
import com.example.bytebattlesmobileapp.presentation.components.StatisticPoint
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent
import com.example.bytebattlesmobileapp.presentation.components.UserHeader

@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        UserHeader(
            "Ivan",
            painter = painterResource(R.drawable.userprofile),
            true
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
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
                    "\uD83D\uDE31 150 очков",
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
                CardProfileStatistic("✅ Победы", "87 (72.5%)")
                CardProfileStatistic("⛔Поражения", "33")
                CardProfileStatistic("\uD83C\uDFF9 Лучшая серия", "9 побед")
            }
            Spacer(Modifier.height(25.dp))
            Text(
                "История очков",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
            )
            Spacer(Modifier.height(15.dp))

            RowProfileStatisticPoint(
                listOf(StatisticPoint(
                    "12.12.1990", "Summa", "Turnir",
                    10, true
                ),  StatisticPoint(
                    "12.12.1990", "Summa", "Turnir",
                    0, false
                ))
            )
            Spacer(Modifier.height(25.dp))
            Text(
                "Достижения",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
            )
            Spacer(Modifier.height(15.dp))
            val achievements = listOf(
                Achievement(R.drawable.firstblood, "Первая кровь"),
                Achievement(R.drawable.firstblood, "Быстрый ученик"),
                Achievement(R.drawable.firstblood, "Решатель проблем"),
                Achievement(R.drawable.firstblood, "Мастер кода"),
                Achievement(R.drawable.firstblood, "Командный игрок"),
                Achievement(R.drawable.firstblood, "Перфекционист"),
                Achievement(R.drawable.firstblood, "Инноватор"),
                Achievement(R.drawable.firstblood, "Легенда")
            )
            AchievementsGrid(achievements)
        }
    }
}

data class Achievement(
    val iconRes: Int,
    val title: String
)

@Preview
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen({})
}