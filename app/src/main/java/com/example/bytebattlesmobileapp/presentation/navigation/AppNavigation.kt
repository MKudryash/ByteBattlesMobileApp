package com.example.bytebattlesmobileapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bytebattlesmobileapp.presentation.screens.AuthScreen
import com.example.bytebattlesmobileapp.presentation.screens.BattleScreen
import com.example.bytebattlesmobileapp.presentation.screens.MainScreen
import com.example.bytebattlesmobileapp.presentation.screens.NewStormScreen
import com.example.bytebattlesmobileapp.presentation.screens.ProfileScreen
import com.example.bytebattlesmobileapp.presentation.screens.SignUpScreen
import com.example.bytebattlesmobileapp.presentation.screens.StartScreen
import com.example.bytebattlesmobileapp.presentation.screens.StatisticsScreen
import com.example.bytebattlesmobileapp.presentation.screens.TaskInfoScreen
import com.example.bytebattlesmobileapp.presentation.screens.TaskScreen
import com.example.bytebattlesmobileapp.presentation.screens.TrainCheckScreen
import com.example.bytebattlesmobileapp.presentation.screens.TrainCloseScreen
import com.example.bytebattlesmobileapp.presentation.screens.TrainInfoScreen
import com.example.bytebattlesmobileapp.presentation.screens.TrainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Start.route
    ) {

        composable(Screen.Start.route) {
            StartScreen(
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateToRegister = {navController.navigate(Screen.Auth.route)}
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToMain = { navController.navigate(Screen.Main.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToMain = { navController.navigate(Screen.Main.route) },
                onNavigateBack = { navController.navigateUp() }
            )
        }

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

        composable(
            Screen.TaskInfo.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskInfoScreen(
                taskId = taskId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Battle.route) {
            BattleScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Train.route) {
            TrainScreen(
                onNavigateToTrainInfo = { trainId ->
                    navController.navigate(Screen.TrainInfo.createRoute(trainId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            Screen.TrainInfo.route,
            arguments = listOf(navArgument("trainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            TrainInfoScreen(
                trainId = trainId,
                onNavigateToTrainCheck = { navController.navigate(Screen.TrainCheck.createRoute(trainId)) },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            Screen.TrainCheck.route,
            arguments = listOf(navArgument("trainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            TrainCheckScreen(
                trainId = trainId,
                onNavigateToTrainClose = { navController.navigate(Screen.TrainClose.createRoute(trainId)) },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            Screen.TrainClose.route,
            arguments = listOf(navArgument("trainId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            TrainCloseScreen(
                trainId = trainId,
                onNavigateToMain = { navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                } },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.NewStorm.route) {
            NewStormScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
