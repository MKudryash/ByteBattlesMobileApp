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
fun TaskScreen(
    onNavigateToTaskInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val tasks = listOf("Задача 1", "Задача 2", "Задача 3", "Задача 4", "Задача 5")

    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Задачи",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(tasks) { task ->
                TaskCard(
                    title = task,
                    subtitle = "Описание задачи",
                    onClick = { onNavigateToTaskInfo(task) }
                )
            }
        }
    }
}