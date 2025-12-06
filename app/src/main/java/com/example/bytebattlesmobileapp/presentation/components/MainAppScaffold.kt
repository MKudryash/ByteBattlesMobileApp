package com.example.bytebattlesmobileapp.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bytebattlesmobileapp.presentation.navigation.AppNavigation
import com.example.bytebattlesmobileapp.presentation.screens.BottomNavigationPanel
import com.example.bytebattlesmobileapp.presentation.screens.NavItem

@Composable
fun MainAppScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomNav = when {
        currentRoute?.startsWith("main") == true -> true
        currentRoute?.startsWith("task") == true && !currentRoute.contains("task_info") -> true
        currentRoute?.startsWith("profile") == true -> true
        currentRoute?.startsWith("statistics") == true -> true
        else -> false
    }

    var selectedItem by remember { mutableStateOf<NavItem>(NavItem.Home) }


    LaunchedEffect(currentRoute) {
        selectedItem = when {
            currentRoute?.startsWith("main") == true -> NavItem.Home
            currentRoute?.startsWith("task") == true && !currentRoute.contains("task_info") -> NavItem.Search
            currentRoute?.startsWith("profile") == true -> NavItem.Profile
            currentRoute?.startsWith("statistics") == true -> NavItem.Settings
            else -> selectedItem
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                BottomNavigationPanel(
                    selectedItem = selectedItem,
                    onItemSelected = { navItem ->
                        selectedItem = navItem
                        when (navItem) {
                            NavItem.Home -> {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                            NavItem.Search -> {
                                navController.navigate(Screen.Task.route) {
                                    popUpTo(Screen.Task.route) { inclusive = true }
                                }
                            }
                            NavItem.Main -> {
                                navController.navigate(Screen.Battle.route)
                            }
                            NavItem.Profile -> {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(Screen.Profile.route) { inclusive = true }
                                }
                            }
                            NavItem.Settings -> {
                                navController.navigate(Screen.Statistics.route) {
                                    popUpTo(Screen.Statistics.route) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C3646))
                .padding(paddingValues)
        ) {
            AppNavigation(navController)
        }
    }
}