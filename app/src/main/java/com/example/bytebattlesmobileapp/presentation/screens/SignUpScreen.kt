package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CustomTextField
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun SignUpScreen(
    onNavigateToMain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBarComponent(
            title = "Регистрация",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль",
                isPassword = true,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Подтвердите пароль",
                isPassword = true,
                modifier = Modifier.padding(bottom = 24.dp)
            )


        }
    }
}