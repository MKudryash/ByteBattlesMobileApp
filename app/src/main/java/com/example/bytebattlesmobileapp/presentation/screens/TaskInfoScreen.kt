package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun TaskInfoScreen(
    taskId: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Информация о задаче",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Задача: $taskId",
               /* style = MaterialTheme.typography.h5,*/
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Подробное описание задачи...",
               /* style = MaterialTheme.typography.body1,*/
                modifier = Modifier.padding(bottom = 24.dp)
            )


        }
    }
}