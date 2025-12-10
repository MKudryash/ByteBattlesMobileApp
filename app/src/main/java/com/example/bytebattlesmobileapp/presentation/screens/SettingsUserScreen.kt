package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val passwordOld = remember { mutableStateOf("") }
    val passwordNew = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }

    val passwordsMatch = passwordNew == passwordConfirm
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    LaunchedEffect(uiState.passwordChangeSuccess) {
        if (uiState.passwordChangeSuccess) {
            // Сброс полей после успешной смены
            passwordOld.value = ""
            passwordNew.value = ""
            passwordConfirm.value = ""

            // Автоматический сброс состояния успеха через 3 секунды
            scope.launch {
                kotlinx.coroutines.delay(3000)
                viewModel.resetPasswordChangeSuccess()
            }
        }
    }
    val userName = uiState.currentUser ?: ""
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        // Первый элемент Column - Box
        Box {
            val roundedShape = RoundedCornerShape(15.dp)
            SingleRoundedCornerBox(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.25f),
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 55.dp,
                bottomEnd = 55.dp,
                {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    )
                    {
                        Image(
                            painter = painterResource(R.drawable.menu_burger),
                            contentDescription = ""
                        )

                        Column (
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Image(
                                modifier = Modifier.size(100.dp),
                                painter =  painterResource(R.drawable.userprofile),
                                contentDescription = "user"
                            )
                            Spacer(Modifier.height(15.dp))
                            Text(
                                userName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                            )
                        }
                        Image(painterResource(R.drawable.man), "")
                    }
                }
            )
        }
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF56565).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.error), // Добавьте свою иконку ошибки
                        contentDescription = "Ошибка",
                        tint = Color(0xFFF56565),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Color(0xFFF56565),
                        fontSize = 14.sp
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            CustomUnderlinedTextField(
                value = passwordOld.value,
                onValueChange = { passwordOld.value = it },
                placeholder = "Старый пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = passwordNew.value,
                onValueChange = { passwordNew.value = it },
                placeholder = "Новый пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )
            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = passwordConfirm.value,
                onValueChange = { passwordConfirm.value = it },
                placeholder = "Подтверждение пароля",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )


            Spacer(modifier = Modifier.height(40.dp))

            ActionButton(
                text = "Изменить пароль".uppercase(),
                onClick = {
                    viewModel.changePassword(passwordOld.value,passwordNew.value)
                },
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
        }
    }
}

private fun AuthViewModel.resetPasswordChangeSuccess() {

}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsUserScreen(
        {}
    )
}