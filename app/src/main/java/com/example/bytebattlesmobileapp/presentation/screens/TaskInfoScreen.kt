package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.domain.model.Task
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.BackArrow
import com.example.bytebattlesmobileapp.presentation.components.CardTest
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent
import com.example.bytebattlesmobileapp.presentation.viewmodel.TaskViewModel
import java.util.UUID

@Composable
fun TaskInfoScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateTrain: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {

    val taskState by viewModel.taskState.collectAsStateWithLifecycle()
    LaunchedEffect(taskId) {
        viewModel.getTaskById(UUID.fromString(taskId))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        when(taskState){
            is TaskViewModel.TaskDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (taskState as TaskViewModel.TaskDetailState.Error).message ?: "Неизвестная ошибка",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ActionButton(
                            text = "Повторить",
                            onClick = {
                                try {
                                    val uuid = UUID.fromString(taskId)
                                    viewModel.getTaskById(uuid)
                                } catch (e: Exception) {
                                    // Обработка ошибки
                                }
                            },
                            color = Color(0xFF5EC2C3),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp)
                        )
                    }
                }

            }
            TaskViewModel.TaskDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5EC2C3))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Загрузка задачи...",
                            color = Color.White
                        )
                    }
                }
            }
            is TaskViewModel.TaskDetailState.Success -> {
                Column {
                    val task = (taskState as TaskViewModel.TaskDetailState.Success).task
                    Header({onNavigateBack},task.title)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Spacer(Modifier.height(40.dp))
                        Text(
                            "Описание",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                        )
                        Text(
                            task.description,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
                        )
                        Spacer(Modifier.height(25.dp))
                        Text(
                            "Пример теста",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                        )
                        LazyColumn {
                            items(task.testCases){
                                if(!it.isExample)
                                {CardTest(it.input, it.output)}
                            }
                        }
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionButton(
                                text = "Начать".uppercase(),
                                onClick = { onNavigateTrain()},
                                color = Color(0xFF5EC2C3),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(60.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun TaskInfoScreenPreview() {
    TaskInfoScreen(
        "", {},{}
    )
}