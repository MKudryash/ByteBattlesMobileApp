// BattleResultScreen.kt
package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun BattleResultScreen(
    isWinner: Boolean,
    taskTitle: String,
    message: String,
    onDismiss: () -> Unit,
    showDialog: Boolean = true
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWinner) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Иконка результата
                    Icon(
                        imageVector = if (isWinner) Icons.Default.AccountCircle else Icons.Default.Warning,
                        contentDescription = "Результат",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Заголовок
                    Text(
                        text = if (isWinner) "ПОБЕДА! 🎉" else "ПОРАЖЕНИЕ",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Название задачи
                    Text(
                        text = taskTitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Сообщение
                    Text(
                        text = message,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Кнопка возврата (появится через 3 секунды)
                    var showButton by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(3000) // Показываем результат 3 секунды
                        showButton = true
                    }

                    if (showButton) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = if (isWinner) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        ) {
                            Text(
                                text = "ВЕРНУТЬСЯ НА ГЛАВНЫЙ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Таймер обратного отсчета
                        var countdown by remember { mutableStateOf(3) }

                        LaunchedEffect(Unit) {
                            repeat(3) {
                                delay(1000)
                                countdown--
                            }
                        }

                        Text(
                            text = "Возврат через $countdown...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}