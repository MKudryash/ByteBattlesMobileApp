package com.example.bytebattlesmobileapp.presentation.screens

import android.media.Image
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.BackArrow
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.RememberMeCheckbox
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMain: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val shouldNavigateToMain by authViewModel.navigateToMain.collectAsStateWithLifecycle()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Следим за ошибками
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMessage ->
            Log.e("AuthScreen", "Auth error: $errorMessage")
        }
    }

    LaunchedEffect(shouldNavigateToMain) {
        if (shouldNavigateToMain) {
            onNavigateToMain()
            authViewModel.navigationHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        // Первый элемент Column - Box
        Box {
            SingleRoundedCornerBox(
                modifier = Modifier.align(Alignment.TopEnd),
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 55.dp,
                bottomEnd = 0.dp,
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 45.dp, start = 20.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            BackArrow({ onNavigateBack() })
                        }

                        Spacer(modifier = Modifier.height(60.dp))

                        Text(
                            "Добро пожаловать обратно!".uppercase(),
                            color = Color.White,
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Text(
                            "Продолжайте свое приключение!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
                        )
                    }
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Отображение ошибки
            uiState.errorMessage?.let { error ->
                ErrorMessageCard(error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            CustomUnderlinedTextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    // Очищаем ошибку при изменении email
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                placeholder = "Почта",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                    // Очищаем ошибку при изменении пароля
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                placeholder = "Пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )

            var rememberMe by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                RememberMeCheckbox(
                    text = "Запомнить меня",
                    isChecked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF5EC2C3),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                ActionButton(
                    text = "ВОЙТИ",
                    onClick = {
                        if (email.value.isBlank() || password.value.isBlank()) {
                            authViewModel.clearError()
                            // Можно показать ошибку о пустых полях
                        } else {
                            authViewModel.login(email.value, password.value)
                        }
                    },
                    color = Color(0xFF5EC2C3),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp),
                    enabled = email.value.isNotBlank() && password.value.isNotBlank() && !uiState.isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LinkText(
                    modifier = Modifier.padding().alpha(0.7f),
                    text = "Забыли пароль?",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ErrorMessageCard(error: String) {
    val parsedError = parseAuthError(error)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF56565).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.error), // Добавьте иконку ошибки
                contentDescription = "Ошибка",
                tint = Color(0xFFF56565),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = parsedError.title,
                    color = Color(0xFFF56565),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = parsedError.message,
                    color = Color(0xFFF56565),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

data class ParsedAuthError(
    val title: String,
    val message: String
)

fun parseAuthError(errorMessage: String): ParsedAuthError {
    return when {
        errorMessage.contains("Invalid credentials", ignoreCase = true) ->
            ParsedAuthError(
                title = "Ошибка авторизации",
                message = "Неверный email или пароль. Проверьте правильность введенных данных."
            )

        errorMessage.contains("Network is unreachable", ignoreCase = true) ||
                errorMessage.contains("Failed to connect", ignoreCase = true) ->
            ParsedAuthError(
                title = "Нет подключения",
                message = "Проверьте подключение к интернету и попробуйте снова."
            )

        errorMessage.contains("timeout", ignoreCase = true) ->
            ParsedAuthError(
                title = "Таймаут соединения",
                message = "Сервер не отвечает. Попробуйте позже."
            )

        else ->  ParsedAuthError(
            title = "Ошибка авторизации",
            message = "Неверный email или пароль. Проверьте правильность введенных данных. Или аккаунта не существует"
        )
    }
}

@Composable
fun LinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color(0xFF5EC2C3),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    MaterialTheme {
        AuthScreen(
            onNavigateBack = {},
            onNavigateToMain = {}
        )
    }
}