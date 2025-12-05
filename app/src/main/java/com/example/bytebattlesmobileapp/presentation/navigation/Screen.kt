package com.example.bytebattlesmobileapp.presentation.navigation

sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Auth : Screen("auth")
    object SignUp : Screen("signup")
    object Main : Screen("main")
    object Task : Screen("task")
    object Profile : Screen("profile")
    object Battle : Screen("battle")
    object Statistics : Screen("statistics")
    object TaskInfo : Screen("task_info/{taskId}") {
        fun createRoute(taskId: String) = "task_info/$taskId"
    }
    object Train : Screen("train")
    object TrainInfo : Screen("train_info/{trainId}") {
        fun createRoute(trainId: String) = "train_info/$trainId"
    }
    object TrainCheck : Screen("train_check/{trainId}") {
        fun createRoute(trainId: String) = "train_check/$trainId"
    }
    object TrainClose : Screen("train_close/{trainId}") {
        fun createRoute(trainId: String) = "train_close/$trainId"
    }
    object NewStorm : Screen("new_storm")
}