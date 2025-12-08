package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.AuthViewModel

@Composable
fun StartScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState = authViewModel.uiState.collectAsState()
    val navigateToMain = authViewModel.navigateToMain.collectAsState()

    // Отслеживаем состояние загрузки и навигации
    LaunchedEffect(uiState.value.isCheckingAuth, navigateToMain.value) {
        // Если проверка завершена И есть токен, переходим на main
        if (!uiState.value.isCheckingAuth && uiState.value.isLoggedIn) {
            onNavigateToMain()
            authViewModel.navigationHandled()
        }

        // Если navigateToMain стало true, также переходим
        if (navigateToMain.value) {
            onNavigateToMain()
            authViewModel.navigationHandled()
        }
    }

    // Показываем индикатор загрузки во время проверки
    if (uiState.value.isCheckingAuth) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        // Показываем основной контент, если проверка завершена
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            SingleRoundedCornerBox(
                modifier = Modifier.align(Alignment.BottomCenter),
                0.dp, 55.dp, 0.dp, 0.dp,{
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 26.dp)
                                .padding(top = 56.dp),
                            text = "Программируй. Соревнуйся. Вдохновляй",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        ActionButton(
                            text = "ВОЙТИ",
                            onClick = { onNavigateToAuth() },
                            color = Color(0xFF2C3646),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            LinkText(
                                modifier = Modifier.padding(),
                                text = "Или создайте аккаунт",
                                onClick = { onNavigateToRegister() },
                                painter = painterResource(R.drawable.arrow_end),
                                colorText = Color(0xff2C3646)
                            )
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun LinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    colorText:Color = Color.White
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = colorText
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = colorText,
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    Font(R.font.ibmplexmono_semibold)
                )
            )
            if (painter != null) {
            Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painter,
                    contentDescription = "Стрелка",
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

// Предварительный просмотр
@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    MaterialTheme {
        StartScreen(
            onNavigateToAuth = {},
            onNavigateToRegister = {},
            {}
        )
    }
}
