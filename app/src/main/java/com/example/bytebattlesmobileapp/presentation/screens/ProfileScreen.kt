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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle() // Добавим этот флаг в ViewModel

    var username by remember { mutableStateOf("") }
    var linkGithub by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Для отслеживания изначальных значений
    var originalUsername by remember { mutableStateOf("") }
    var originalLinkGithub by remember { mutableStateOf("") }
    var originalCountry by remember { mutableStateOf("") }
    var originalDescription by remember { mutableStateOf("") }

    // Состояния для уведомлений
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Заполняем поля при загрузке данных
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Success) {
            val data = (uiState as ProfileViewModel.ProfileUiState.Success).data
            username = data.profile.userName!!
            linkGithub = data.profile.gitHubUrl ?: ""
            country = data.profile.country ?: ""
            description = data.profile.bio ?: ""

            // Сохраняем оригинальные значения для сравнения
            originalUsername = data.profile.userName!!
            originalLinkGithub = data.profile.gitHubUrl ?: ""
            originalCountry = data.profile.country ?: ""
            originalDescription = data.profile.bio ?: ""
        }
    }

    // Обработка успешного обновления
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            showSuccessMessage = true
            // Автоматически скрываем через 3 секунды
            kotlinx.coroutines.delay(3000)
            showSuccessMessage = false
        }
    }

    // Обработка ошибок
    LaunchedEffect(error) {
        error?.let { errorMsg ->
            showErrorMessage = true
            errorMessage = errorMsg
            // Автоматически скрываем через 5 секунд
            kotlinx.coroutines.delay(5000)
            showErrorMessage = false
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                is ProfileViewModel.ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5EC2C3))
                    }
                }

                is ProfileViewModel.ProfileUiState.Empty -> {
                    EmptyProfileView(onNavigateBack)
                }

                is ProfileViewModel.ProfileUiState.Error -> {
                    ErrorProfileView(
                        message = (uiState as ProfileViewModel.ProfileUiState.Error).message,
                        onRetry = { viewModel.loadProfileData() },
                        onNavigateBack = onNavigateBack
                    )
                }

                is ProfileViewModel.ProfileUiState.Success -> {
                    val data = (uiState as ProfileViewModel.ProfileUiState.Success).data

                    // Первый элемент Column - Box
                    Box {
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
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.menu_burger),
                                        contentDescription = "",
                                        modifier = Modifier.clickable { onNavigateBack() }
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            modifier = Modifier.size(100.dp),
                                            painter = painterResource(R.drawable.userprofile),
                                            contentDescription = "user"
                                        )
                                        Spacer(Modifier.height(15.dp))
                                        Text(
                                            data.profile.userName!!,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                                        )
                                    }
                                    Image(
                                        painter = painterResource(R.drawable.man),
                                        contentDescription = "",
                                        modifier = Modifier.clickable {
                                            // Навигация к статистике или другим экранам
                                        }
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
                            value = username,
                            onValueChange = { username = it },
                            placeholder = "Имя",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        CustomUnderlinedTextField(
                            value = linkGithub,
                            onValueChange = { linkGithub = it },
                            placeholder = "Github",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        CustomUnderlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            placeholder = "Страна",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        CustomUnderlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Описание",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp),
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF5EC2C3)
                            )
                        } else {
                            // Проверяем, изменились ли данные
                            val hasChanges = username != originalUsername ||
                                    linkGithub != originalLinkGithub ||
                                    country != originalCountry ||
                                    description != originalDescription

                            ActionButton(
                                text = if (hasChanges) "Сохранить изменения" else "Нет изменений",
                                onClick = {
                                    if (hasChanges) {
                                        viewModel.updateProfile(
                                            userName = if (username != originalUsername) username else null,
                                            link = if (linkGithub != originalLinkGithub) linkGithub else null,
                                            country = if (country != originalCountry) country else null,
                                            bio = if (description != originalDescription) description else null
                                        )
                                    }
                                },
                                color = if (hasChanges) Color(0xFF5EC2C3) else Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(60.dp),
                                enabled = hasChanges && !isLoading
                            )
                        }
                    }
                }
            }
        }

        // Уведомление об успешном сохранении
        if (showSuccessMessage) {
            SuccessNotification(
                message = "Профиль успешно обновлен",
                onDismiss = { showSuccessMessage = false },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            )
        }

        // Уведомление об ошибке
        if (showErrorMessage) {
            ErrorNotification(
                message = errorMessage,
                onDismiss = {
                    showErrorMessage = false
                    viewModel.clearError()
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            )
        }
    }
}
@Composable
fun EmptyProfileView(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.userprofile),
            contentDescription = "Empty profile",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Профиль не найден",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Войдите в систему или создайте профиль",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        ActionButton(
            text = "Войти",
            onClick = { /* Навигация на экран логина */ },
            color = Color(0xFF5EC2C3),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        )
    }
}

@Composable
fun ErrorProfileView(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            tint = Color.Red
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ошибка загрузки",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                text = "Назад",
                onClick = onNavigateBack,
                color = Color.Gray,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            )
            ActionButton(
                text = "Повторить",
                onClick = onRetry,
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            )
        }
    }
}
@Composable
fun SuccessNotification(
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
            .fillMaxWidth(0.9f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.check), // Используем вашу иконку
                contentDescription = "Успех",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.close), // Иконка закрытия
                contentDescription = "Закрыть",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
fun ErrorNotification(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF44336).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.error), // Используем вашу иконку
                contentDescription = "Ошибка",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.close), // Иконка закрытия
                contentDescription = "Закрыть",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}
@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        {}
    )
}