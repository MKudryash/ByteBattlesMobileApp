package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CustomTextField

@Composable
fun AuthScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Авторизация",
            /*style = MaterialTheme.typography.h4,*/
            modifier = Modifier.padding(bottom = 32.dp)
        )

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
            modifier = Modifier.padding(bottom = 24.dp)
        )


    }
}