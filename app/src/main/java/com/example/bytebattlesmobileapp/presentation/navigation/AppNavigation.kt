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
                onNavigateToMenu = { it -> navController.navigate(Screen.SideMenu.createRoute(it)) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToBattle = { navController.navigate(Screen.Battle.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToNewStorm = { navController.navigate(Screen.NewStorm.route) }
            )
        }

        composable(Screen.Task.route,
            arguments = listOf(
                navArgument("userName") { type = NavType.StringType }
            )) {backStackEntry ->
            val name = backStackEntry.arguments?.getString("userName")
            TaskScreen(
                onNavigateToTaskInfo = { taskId ->
                    navController.navigate(Screen.TaskInfo.createRoute(taskId))
                },
                onNavigateSideMenu = { navController.navigate(Screen.SideMenu.route) },
                userName = name
            )
        }
        composable(
            Screen.SideMenu.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType }
            )) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            SideScreen(
                name = name,
                onNavigateToTasks = { it-> navController.navigate(Screen.Task.createRoute(it)) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Setting.route) },
                onNavigateToLogOut = {navController.navigate(Screen.Start.route)}
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.Setting.route) {
            SettingsUserScreen (
                onNavigateBack = { navController.navigateUp() }
            )

        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            Screen.Battle.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")

            BattleContainerScreen(
                navController = navController,
                initialRoomId = roomId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToGame = { taskId, roomId ->
                    // Переходим на экран TrainBattle с taskId
                    navController.navigate(Screen.TrainBattle.createRoute(taskId, roomId)) {
                    }
                }
            )
        }
        composable(
            Screen.TrainBattle.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val roomId = backStackEntry.arguments?.getString("roomId")

            TrainBattleScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0)
                    }
                },
                taskId = taskId,
                roomId = roomId, // Передаем roomId
                onNavigateMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(
            route = Screen.TrainBattle.route + "/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            // Получаем roomId из query параметров
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""

            TrainBattleScreen(
                onNavigateBack = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0)
                    }
                },
                taskId = taskId,
                roomId = roomId,
                onNavigateMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0)
                    }
                }
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
                onNavigateTrain = { taskId ->
                    navController.navigate(Screen.Train.createRoute(taskId))
                },
            )
        }

        composable(
            Screen.Train.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TrainScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        route = Screen.Main.route,
                        inclusive = false
                    ) ?: navController.navigate(Screen.Main.route)
                },
                taskId = taskId,
                onNavigateMain = {navController.navigate(Screen.Main.route)}
            )
        }

        composable(
            Screen.TrainInfo.route,
            arguments = listOf(navArgument("trainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            TrainInfoScreen(
                trainId = trainId,
                onNavigateToTrainCheck = {
                    navController.navigate(
                        Screen.TrainInfo.createRoute(
                            trainId
                        )
                    )
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}