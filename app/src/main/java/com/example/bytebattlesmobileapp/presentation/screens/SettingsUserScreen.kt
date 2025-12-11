package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.bytebattlesmobileapp.presentation.components.ValidationErrorMessage
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

    // Состояния для ошибок валидации
    val passwordMismatchError = remember { mutableStateOf<String?>(null) }
    val samePasswordError = remember { mutableStateOf<String?>(null) }
    val shortPasswordError = remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Функция для сброса всех ошибок
    fun clearAllErrors() {
        passwordMismatchError.value = null
        samePasswordError.value = null
        shortPasswordError.value = null
    }

    // Отслеживаем успешную смену пароля
    LaunchedEffect(uiState.passwordChangeSuccess) {
        if (uiState.passwordChangeSuccess) {
            // Сброс полей после успешной смены
            passwordOld.value = ""
            passwordNew.value = ""
            passwordConfirm.value = ""
            clearAllErrors()
        }
    }

    // Отслеживаем состояние ошибки из ViewModel
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Автоматически скрываем ошибку через 5 секунд
            scope.launch {
                kotlinx.coroutines.delay(5000)
                viewModel.clearError()
            }
        }
    }

    val userName = uiState.currentUser ?: ""

    // Функция для валидации паролей
    fun validatePasswords(): Boolean {
        clearAllErrors()

        // Проверка на пустые поля
        if (passwordOld.value.isEmpty() || passwordNew.value.isEmpty() || passwordConfirm.value.isEmpty()) {
            return false // Разрешаем отправку, но валидация будет на сервере
        }

        // Проверка на совпадение нового и подтвержденного пароля
        if (passwordNew.value != passwordConfirm.value) {
            passwordMismatchError.value = "Новый пароль и подтверждение не совпадают"
            return false
        }

        // Проверка на длину нового пароля
        if (passwordNew.value.length < 6) {
            shortPasswordError.value = "Пароль должен содержать минимум 6 символов"
            return false
        }

        // Проверка, что старый и новый пароли разные
        if (passwordOld.value == passwordNew.value) {
            samePasswordError.value = "Новый пароль должен отличаться от старого"
            return false
        }

        return true
    }

    // Функция для обработки нажатия кнопки
    fun onChangePasswordClick() {
        if (validatePasswords()) {
            viewModel.changePassword(passwordOld.value, passwordNew.value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
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
                            contentDescription = "",
                            Modifier.clickable(onClick = {
                                onNavigateBack()
                            })
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

        // Отображение сообщений об успехе или ошибке
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.passwordChangeSuccess) {
                SuccessMessage(
                    message = "Пароль успешно изменен!",
                    onDismiss = { viewModel.resetPasswordChangeSuccess() }
                )
            }

            // Показываем ошибки валидации
            passwordMismatchError.value?.let { error ->
                ValidationErrorMessage(
                    message = error,
                    onDismiss = { passwordMismatchError.value = null }
                )
            }

            samePasswordError.value?.let { error ->
                ValidationErrorMessage(
                    message = error,
                    onDismiss = { samePasswordError.value = null }
                )
            }

            shortPasswordError.value?.let { error ->
                ValidationErrorMessage(
                    message = error,
                    onDismiss = { shortPasswordError.value = null }
                )
            }

            // Ошибка из ViewModel
            uiState.errorMessage?.let { error ->
                ErrorMessage(
                    message = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            CustomUnderlinedTextField(
                value = passwordOld.value,
                onValueChange = {
                    passwordOld.value = it
                    clearAllErrors() // Сбрасываем ошибки при вводе
                },
                placeholder = "Старый пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = passwordNew.value,
                onValueChange = {
                    passwordNew.value = it
                    clearAllErrors() // Сбрасываем ошибки при вводе
                },
                placeholder = "Новый пароль",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = passwordConfirm.value,
                onValueChange = {
                    passwordConfirm.value = it
                    clearAllErrors() // Сбрасываем ошибки при вводе
                },
                placeholder = "Подтверждение пароля",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(40.dp))

            ActionButton(
                text = "Изменить пароль".uppercase(),
                onClick = ::onChangePasswordClick, // Используем функцию валидации
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp),
                enabled = !uiState.isLoading && !uiState.isChangingPassword &&
                        passwordOld.value.isNotEmpty() &&
                        passwordNew.value.isNotEmpty() &&
                        passwordConfirm.value.isNotEmpty()
            )

            // Индикатор загрузки
            if (uiState.isLoading || uiState.isChangingPassword) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = Color(0xFF5EC2C3),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SuccessMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        // Автоматически скрываем через 3 секунды
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF48BB78).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.check), // Добавьте иконку успеха
                contentDescription = "Успех",
                tint = Color(0xFF48BB78),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                color = Color(0xFF48BB78),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF56565).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.error),
                contentDescription = "Ошибка",
                tint = Color(0xFFF56565),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                color = Color(0xFFF56565),
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.close), // Добавьте иконку закрытия
                contentDescription = "Закрыть",
                tint = Color(0xFFF56565),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}

private fun validatePasswords(oldPassword: String, newPassword: String, confirmPassword: String): Boolean {
    return when {
        oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
            // Ошибку покажет ViewModel
            true // Разрешаем отправку, валидация будет на сервере
        }
        newPassword.length < 6 -> {
            // Ошибку покажет UI
            false
        }
        oldPassword == newPassword -> {
            false
        }
        newPassword != confirmPassword -> {
            false
        }
        else -> true
    }
}

// Если у вас нет CircularProgressIndicator, добавьте импорт:
// import androidx.compose.material3.CircularProgressIndicator

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsUserScreen(
        {}
    )
}