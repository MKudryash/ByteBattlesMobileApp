package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.RememberMeCheckbox
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox

@Composable
fun SignUpScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateBackMain: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

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
                        // Box со стрелкой
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 45.dp, start = 20.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Color(0xFF5EC2C3))
                                    .clickable(
                                        onClick =
                                        {
                                            onNavigateBack()
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.arrow_start),
                                    contentDescription = "Стрелка",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(90.dp))

                        Text(
                            "Создайте аккаунт".uppercase(),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            fontSize = 30.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                            lineHeight = 40.sp
                        )
                    }
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            CustomUnderlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = "Имя",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )
            CustomUnderlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Почта",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )
            CustomUnderlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )
            CustomUnderlinedTextField(
                value = passwordConfirm.value,
                onValueChange = { passwordConfirm.value = it },
                placeholder = "Подтверждение пароля",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                RememberMeCheckbox(
                    text = "Я ознакомился с правилами и принимаю все их положения",
                    isChecked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))



            Spacer(modifier = Modifier.height(10.dp))

            ActionButton(
                text = "зарегистрироваться".uppercase(),
                onClick = { onNavigateBackMain()},
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val annotatedText = buildAnnotatedString {
                append("У вас уже есть учетная запись?")

                pushStringAnnotation(
                    tag = "login",
                    annotation = "login_click"
                )
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF5EC2C3),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Войдите!")
                }
                pop()
            }

            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "login",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let { annotation ->
                        onNavigateToAuth()
                    }
                },
                modifier = Modifier.padding(horizontal = 25.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(
            onNavigateToAuth = {},
            onNavigateBackMain = {},
            onNavigateBack = {}
        )
    }
}