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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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

    var username by remember { mutableStateOf("") }
    var linkGithub by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Заполняем поля при загрузке данных
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Success) {
            val data = (uiState as ProfileViewModel.ProfileUiState.Success).data
            username = data.profile.userName
            linkGithub = data.profile.gitHubUrl?: ""
            country = data.profile.country?:""
            description = data.profile.bio ?: ""
        }
    }

    // Обработка ошибок
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Показать Snackbar или диалог
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
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
                                        data.profile.userName,
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
                        ActionButton(
                            text = "Сохранить".uppercase(),
                            onClick = {
                                viewModel.updateProfile(
                                    userName = if (username != data.profile.userName) username else null,
                                    link = if (linkGithub != data.profile.gitHubUrl) linkGithub else null,
                                    country = if (country != data.profile.country) country else null,
                                    bio = if (description != data.profile.bio) description else null
                                )
                            },
                            color = Color(0xFF5EC2C3),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        )
                    }
                }
            }
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

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        {}
    )
}