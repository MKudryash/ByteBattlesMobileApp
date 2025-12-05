package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun BattleScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Битвы",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TaskCard(
                title = "Активные битвы",
                subtitle = "3 битвы в процессе"
            )

            TaskCard(
                title = "Завершенные битвы",
                subtitle = "12 завершенных битв"
            )


        }
    }
}
