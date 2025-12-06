// Типы экранов для управления навигацией
sealed class ScreenType {
    object Auth : ScreenType()           // Экран без панели (аутентификация)
    object WithoutBottomNav : ScreenType() // Экран без панели (например, Train, TaskInfo)
    object WithBottomNav : ScreenType()    // Экран с панелью (основные экраны)
}

// Расширим Screen для добавления типа
sealed class Screen(val route: String, val screenType: ScreenType) {
    object Start : Screen("start", ScreenType.Auth)
    object Auth : Screen("auth", ScreenType.Auth)
    object SignUp : Screen("signup", ScreenType.Auth)

    object Main : Screen("main", ScreenType.WithBottomNav)
    object Task : Screen("task", ScreenType.WithBottomNav)
    object Profile : Screen("profile", ScreenType.WithBottomNav)
    object Statistics : Screen("statistics", ScreenType.WithBottomNav)

    object Battle : Screen("battle", ScreenType.WithoutBottomNav)
    object NewStorm : Screen("new_storm", ScreenType.WithoutBottomNav)
    object TaskInfo : Screen("task_info/{taskId}", ScreenType.WithoutBottomNav) {
        fun createRoute(taskId: String) = "task_info/$taskId"
    }
    object Train : Screen("train", ScreenType.WithoutBottomNav)
    object TrainInfo : Screen("train_info/{trainId}", ScreenType.WithoutBottomNav) {
        fun createRoute(trainId: String) = "train_info/$trainId"
    }
}