// BattleChoiceScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.BattleLobbyViewModel

@Composable
fun BattleSelectionScreen(
    onNavigateBack: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomClick: () -> Unit,
    viewModel: BattleLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя часть с заголовком
            SingleRoundedCornerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f),
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 40.dp,
                bottomEnd = 40.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Иконка битвы
                    Image(
                        painter = painterResource(R.drawable.icons_all_languages), // Используйте свою иконку
                        contentDescription = "Битва программистов",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Битва Программистов",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Статус подключения
                    ConnectionStatus(
                        isConnected = uiState.isConnected,
                        isLoading = uiState.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Основное содержимое
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Кнопка создания комнаты
                ActionButton(
                    text = "Создать комнату".uppercase(),
                    onClick = onCreateRoomClick,
                    color = Color(0xFF5EC2C3),

                    enabled = !uiState.isLoading
                )
                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    color = Color(0xFF4A5568),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Кнопка присоединения к комнате
                ActionButton(
                    text = "Присоединиться к комнате".uppercase(),
                    onClick = onJoinRoomClick,
                    color = Color(0xFF3C4B60),

                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(30.dp))




                // Информация о статусе
                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF5EC2C3),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Подключение к серверу...",
                            color = Color(0xFFA0AEC0),
                            fontSize = 14.sp
                        )
                    }
                } else if (!uiState.isConnected) {
                    Text(
                        text = "Для подключения требуется интернет",
                        color = Color(0xFFF56565),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Кнопка назад
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.arrow_start),
                contentDescription = "Назад",
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onNavigateBack)
                    .padding(8.dp)
            )
        }
    }
}
