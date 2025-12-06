package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.CardNewsOrTask
import com.example.bytebattlesmobileapp.presentation.components.UserHeader
import com.example.bytebattlesmobileapp.presentation.components.UserTopCard

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        UserHeader("Ivan", painter = null, showIcon = true)
        LazyColumn {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Text(
                        "Топ игроков",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )

                    //Топ 5 user
                    UserTopCard(
                        "NickUser",
                        painter = painterResource(R.drawable.icon_man),
                        "120"
                    )
                    UserTopCard(
                        "NickUser",
                        painter = painterResource(R.drawable.icon_man),
                        "120"
                    )
                    UserTopCard(
                        "NickUser",
                        painter = painterResource(R.drawable.icon_man),
                        "120"
                    )


                    Text(
                        "Новости",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    //Новости
                    CardNewsOrTask(
                        "nameOfNews",
                        "date",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {  }
                    )
                    CardNewsOrTask(
                        "nameOfNews",
                        "date",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {  }
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        {},
        {}, {},
        {}, {}, {}
    )
}


