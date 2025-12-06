package com.example.bytebattlesmobileapp.presentation.screens

import CodeEditor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.CircleButton
import com.example.bytebattlesmobileapp.presentation.components.CustomInfoDialog
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.wakaztahir.codeeditor.highlight.model.CodeLang

@Composable
fun TrainScreen(
    onNavigateToTrainInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var nameTask = remember { mutableStateOf("Name of Task") }
    var finishTask by remember { mutableStateOf(false) }
    var submitCode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header({ onNavigateBack() }, nameTask.value)

            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "Решение",
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold))
                    )
                }

                CodeEditor(language = CodeLang.CSharp)
            }
        }

        // Три круга в правом нижнем углу экрана
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "00:30",
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
                )

                CustomInfoDialog(
                    showDialog = finishTask,
                    onDismiss = { finishTask = false },
                    onNavigateToInfo = {

                        onNavigateToTrainInfo("task_details")
                        finishTask = false
                    },
                    title = "Закончить?",
                    text = "Завершить выполнение задачи??"
                )


                CircleButton(
                    painterResource(R.drawable.close),

                    onClick = { finishTask = true },
                    color = Color(0xFFF44336)
                )



                CircleButton(
                    painterResource(R.drawable.inform),

                    onClick = {},
                    color = Color(0xFFFF9800)
                )

                CustomInfoDialog(
                    showDialog = submitCode,
                    onDismiss = { submitCode = false },
                    onNavigateToInfo = {
                        onNavigateToTrainInfo("task_details")
                        submitCode = false
                    },
                    title = "Отправить код на проверку?",
                    text = "Уверены?"
                )
                CircleButton(
                    painterResource(R.drawable.succes),

                    onClick = { submitCode = true },

                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Preview
@Composable
fun TrainScreenPreview() {
    TrainScreen({}, {})
}