package com.example.bytebattlesmobileapp.presentation.components

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
import androidx.compose.ui.text.style.TextAlign
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


@Composable
fun CustomTimerBar(
    timeRemaining: Int,
    totalTime: Int,
    onPauseResume: () -> Unit,
    isRunning: Boolean
) {
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val progress = (timeRemaining.toFloat() / totalTime.toFloat())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A4659)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Верхняя строка с временем и кнопкой
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    color = if (timeRemaining < 60) Color(0xFFFF5722) else Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Прогресс-бар
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    progress > 0.5 -> Color(0xFF4CAF50)
                    progress > 0.2 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                },
                trackColor = Color(0xFF2C3646)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Текст состояния
            Text(
                text = when {
                    !isRunning -> "Таймер на паузе"
                    timeRemaining < 60 -> "Осталось менее минуты!"
                    timeRemaining < 300 -> "Последние 5 минут"
                    else -> "Таймер запущен"
                },
                color = when {
                    !isRunning -> Color(0xFF9E9E9E)
                    timeRemaining < 60 -> Color(0xFFFF5722)
                    timeRemaining < 300 -> Color(0xFFFF9800)
                    else -> Color(0xFF4CAF50)
                },
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
