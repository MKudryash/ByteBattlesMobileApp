package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.SideMenu
import com.example.bytebattlesmobileapp.presentation.components.UserAvatar
import com.example.bytebattlesmobileapp.presentation.viewmodel.AuthViewModel
import java.nio.file.WatchEvent

@Composable
fun SideScreen(
    name: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToTasks: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogOut: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin && !uiState.isLoggedIn) {
            onNavigateToLogOut()
            viewModel.loginNavigationHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
            .padding(30.dp)
    ) {

        Spacer(Modifier.height(70.dp))
        UserAvatar(
            modifier = Modifier.size(100.dp),
            initials = name,
            imagePainter = painterResource(R.drawable.userprofile),// или painterResource
            showIcon = true
        )
        Spacer(Modifier.height(30.dp))

        Text(
            text = name?: "",
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
        )

        Spacer(Modifier.height(50.dp))


        var menu = listOf(
            MenuData(
                painterResource(R.drawable.menu_profile),
                "Профиль"
            ) { onNavigateToProfile() },
            MenuData(
                painterResource(R.drawable.menu_tasks),
                "Задания"
            ) { onNavigateToTasks(name?:"") },
            MenuData(
                painterResource(R.drawable.menu_favorite),
                "Избранное"
            ) {},
            MenuData(
                painterResource(R.drawable.menu_profile),
                "Награды"
            ) {  },
            MenuData(
                painterResource(R.drawable.menu_notification),
                "Уведомления"){},
            MenuData(
                painterResource(R.drawable.menu_setting),
                "Настройки"
            ) { onNavigateToSettings() }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(menu) {
                SideMenu(it.icon, it.title, it.action)

            }
        }
        Spacer(Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(50))
                .background(color = Color(0xFF5EC2C3))
        )
        Spacer(Modifier.height(30.dp))
        SideMenu(
            painterResource(R.drawable.mune_exit),
            "Выход", {
                viewModel.logout()
               })

    }
}

data class MenuData(
    val icon: Painter,
    val title: String,
    val action: () -> Unit
)

@Preview
@Composable
fun SideScreenPreview() {
    SideScreen("", {}, {}, {}, {})
}