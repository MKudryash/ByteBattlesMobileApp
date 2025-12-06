package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun BottomNavigationPanel(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
            .background(Color.Transparent),
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C3646)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C3646)), // Повторяем фон,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Первый элемент (Домой)
            NavigationItem(
                item = NavItem.Home,
                selected = selectedItem == NavItem.Home,
                onClick = { onItemSelected(NavItem.Home) },
                icon = painterResource(R.drawable.home)
            )

            // Второй элемент (Поиск)
            NavigationItem(
                item = NavItem.Search,
                selected = selectedItem == NavItem.Search,
                onClick = { onItemSelected(NavItem.Search) },
                icon = painterResource(R.drawable.menu_tasks)
            )

            // Центральный элемент (Главный)
            Box(

                contentAlignment = Alignment.Center
            ) {


                // Основной круг
                Card(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF53C2C3)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    onClick = { onItemSelected(NavItem.Main) },
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.middle), // Ваша иконка для главной
                            contentDescription = "Главная",
                            tint = Color(0xFF2C3646),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Четвертый элемент (Профиль)
            NavigationItem(
                item = NavItem.Profile,
                selected = selectedItem == NavItem.Profile,
                onClick = { onItemSelected(NavItem.Profile) },
                icon = painterResource(R.drawable.statisctic)
            )

            // Пятый элемент (Настройки)
            NavigationItem(
                item = NavItem.Settings,
                selected = selectedItem == NavItem.Settings,
                onClick = { onItemSelected(NavItem.Settings) },
                icon = painterResource(R.drawable.user)
            )
        }
    }
}

@Composable
fun NavigationItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
    icon: Painter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(64.dp)
            .height(56.dp)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(5.dp)
                    .background(
                        color = Color(0xFF53C2C3),
                        shape = RoundedCornerShape(50)
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 5.dp)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}

sealed class NavItem(val title: String, val route: String) {
    object Home : NavItem("Главная", Screen.Main.route)
    object Search : NavItem("Задачи", Screen.Task.route)
    object Main : NavItem("Баттл", Screen.Battle.route)
    object Profile : NavItem("Профиль", Screen.Profile.route)
    object Settings : NavItem("Статистика", Screen.Statistics.route)
}

@Preview
@Composable
fun MainScreenWithBottomNav() {
    var selectedItem by remember { mutableStateOf<NavItem>(NavItem.Home) }

    Scaffold(
        bottomBar = {
            BottomNavigationPanel(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Выбран: ${selectedItem.title}",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
            )
        }
    }
}


// Демонстрация всех вариантов
@Preview
@Composable
fun BottomNavigationPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {

        BottomNavigationPanel(
            selectedItem = NavItem.Home,
            onItemSelected = {}
        )


    }
}

/*
@Preview
@Composable
fun MainScreenPreview() {
    MainScreenWithBottomNav()
}*/
