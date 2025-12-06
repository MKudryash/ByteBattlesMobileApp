package com.example.bytebattlesmobileapp.presentation.screens

import android.media.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.BackArrow
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.RememberMeCheckbox
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox

@Composable
fun AuthScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMain: () -> Unit
) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        // Первый элемент Column - Box
        Box {
            val roundedShape = RoundedCornerShape(15.dp)
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
                            BackArrow(
                                { onNavigateBack() })
                        }

                        Spacer(modifier = Modifier.height(60.dp))

                        Text(
                            "Добро пожаловать обратно!".uppercase(),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Text(
                            "Продолжайте свое приключение!",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
                        )
                    }
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(100.dp))
            CustomUnderlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Почта",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
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

            ActionButton(
                text = "ВОЙТИ",
                onClick = { onNavigateToMain() },
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End // ← Выравнивание по правому краю
            ) {
                LinkText(
                    modifier = Modifier.padding().alpha(0.7f),
                    text = "Забыли пароль?",
                    onClick = { }
                )
            }
        }
    }

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