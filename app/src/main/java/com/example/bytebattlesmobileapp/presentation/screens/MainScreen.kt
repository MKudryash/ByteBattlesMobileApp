package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bytebattlesmobileapp.presentation.components.NotificationBadge
import com.example.bytebattlesmobileapp.presentation.components.SideMenu
import com.example.bytebattlesmobileapp.presentation.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToTask: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBattle: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToTrain: () -> Unit,
    onNavigateToNewStorm: () -> Unit
) {
    var showSideMenu by remember { mutableStateOf(false) }

    SideMenu(
        isOpen = showSideMenu,
        onClose = { showSideMenu = false },
        menuItems = listOf(
            "Профиль" to onNavigateToProfile,
            "Статистика" to onNavigateToStatistics,
            "Тренировки" to onNavigateToTrain,
            "Новый шторм" to onNavigateToNewStorm
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Главная") },
                navigationIcon = {
                    IconButton(onClick = { showSideMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Уведомления */ }) {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            NotificationBadge(count = 3)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Быстрый доступ к задачам
            TaskCard(
                title = "Мои задачи",
                subtitle = "5 активных задач",
                onClick = onNavigateToTask
            )

            // Быстрый доступ к битвам
            TaskCard(
                title = "Битвы",
                subtitle = "3 активные битвы",
                onClick = onNavigateToBattle
            )

            // Статистика
            TaskCard(
                title = "Статистика",
                subtitle = "Просмотр прогресса",
                onClick = onNavigateToStatistics
            )
        }
    }
}