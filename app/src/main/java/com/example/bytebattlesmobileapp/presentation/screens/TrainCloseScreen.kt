package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun TrainCloseScreen(
    trainId: String,
    onNavigateToMain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Завершение тренировки",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Тренировка завершена!",
               /* style = MaterialTheme.typography.h4,*/
                color = Color.Green,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Результаты
            TaskCard(
                title = "Результаты тренировки",
                subtitle = "Точность: 92%\nВремя: 14:32\nОчки: 1250"
            )

            Spacer(modifier = Modifier.height(32.dp))


        }
    }
}
