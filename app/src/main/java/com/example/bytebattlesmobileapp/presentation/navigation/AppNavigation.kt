package com.example.bytebattlesmobileapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bytebattlesmobileapp.presentation.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Start.route // Или другой стартовый экран
    ) {
        // Аутентификационные экраны (без панели)
        composable(Screen.Start.route) {
            StartScreen(
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateToRegister = { navController.navigate(Screen.SignUp.route) },
                onNavigateToMain = { navController.navigate(Screen.Main.route) }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) // Очищаем весь стек до главного экрана
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateBack = { navController.navigateUp() },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Основные экраны с панелью навигации
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToTask = { navController.navigate(Screen.Task.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToBattle = { navController.navigate(Screen.Battle.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToTrain = { navController.navigate(Screen.Train.route) },
                onNavigateToNewStorm = { navController.navigate(Screen.NewStorm.route) }
            )
        }

        composable(Screen.Task.route) {
            TaskScreen(
                onNavigateToTaskInfo = { taskId ->
                    navController.navigate(Screen.TaskInfo.createRoute(taskId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Экран без панели навигации
        composable(Screen.Battle.route) {
            BattleScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateTrain = { navController.navigate(Screen.Train.route) },
                onNavigateLobby = {navController.navigate(Screen.BattleLobby.route)}
            )
        }
        composable(Screen.BattleLobby.route) {
            BattleLobbyScreen (
                onNavigateBack = { navController.navigateUp() },
            )
        }

        // Остальные экраны без панели
        composable(
            Screen.TaskInfo.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskInfoScreen(
                taskId = taskId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateTrain = { navController.navigate(Screen.Train.route) },
            )
        }

        composable(Screen.Train.route) {
            TrainScreen(
                onNavigateToTrainInfo = { trainId ->
                    navController.navigate(Screen.TrainInfo.createRoute(trainId))
                },
                onNavigateBack = {
                    // Возвращаемся к последнему экрану с панелью
                    navController.popBackStack(
                        route = Screen.Main.route,
                        inclusive = false
                    ) ?: navController.navigate(Screen.Main.route)
                }
            )
        }

        composable(
            Screen.TrainInfo.route,
            arguments = listOf(navArgument("trainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            TrainInfoScreen(
                trainId = trainId,
                onNavigateToTrainCheck = { navController.navigate(Screen.TrainInfo.createRoute(trainId)) },
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}