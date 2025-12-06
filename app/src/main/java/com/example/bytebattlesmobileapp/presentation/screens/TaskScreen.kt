package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.bytebattlesmobileapp.presentation.components.CardLanguage
import com.example.bytebattlesmobileapp.presentation.components.CardNewsOrTask
import com.example.bytebattlesmobileapp.presentation.components.UserHeader

@Composable
fun TaskScreen(
    onNavigateToTaskInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
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
                        "Язык программирования",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    Spacer(Modifier.height(12.dp))
                    //Список язык программирования
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        itemsIndexed(listOf("Python", "Java", "Kotlin", "C++")) { index, language ->

                            CardLanguage(
                                painter = painterResource(R.drawable.csharp),
                                language,
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index }
                            )

                        }
                    }


                    Text(
                        "Задачи",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    CardNewsOrTask(
                        "Name of task",
                        "Easy",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {onNavigateToTaskInfo("name")}
                    )
                    CardNewsOrTask(
                        "Name of task",
                        "Easy",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {onNavigateToTaskInfo("name")}
                    )
                    CardNewsOrTask(
                        "Name of task",
                        "Easy",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {onNavigateToTaskInfo("name")}
                    )
                    CardNewsOrTask(
                        "Name of task",
                        "Easy",
                        "Description about task. Description about task. Description about task.Description about task. Description about task..",
                        {onNavigateToTaskInfo("name")}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TaskScreenPreview(){
    TaskScreen({},{})
}