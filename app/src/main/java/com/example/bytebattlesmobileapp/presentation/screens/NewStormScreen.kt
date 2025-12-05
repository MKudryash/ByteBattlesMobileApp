package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun NewStormScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarComponent(
                title = "Новый шторм",
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
                text = "Новый режим 'Шторм'",
               /* style = MaterialTheme.typography.h4,*/
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Интенсивная тренировка на время",
               /* style = MaterialTheme.typography.body1,*/
                modifier = Modifier.padding(bottom = 32.dp)
            )


        }
    }
}