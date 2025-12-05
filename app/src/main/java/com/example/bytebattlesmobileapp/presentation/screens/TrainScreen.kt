package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun TrainScreen(
    onNavigateToTrainInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val trains = listOf("Тренировка 1", "Тренировка 2", "Тренировка 3")

    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Тренировки",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(trains) { train ->
                TaskCard(
                    title = train,
                    subtitle = "Описание тренировки",
                    onClick = { onNavigateToTrainInfo(train) }
                )
            }
        }
    }
}