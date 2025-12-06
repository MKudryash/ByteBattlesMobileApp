package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.BackArrow
import com.example.bytebattlesmobileapp.presentation.components.CardTest
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun TaskInfoScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateTrain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Column {
            Header({onNavigateBack},"Name of Task")
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
                    "sjndfkgsdnfbmv bnbfdskjgsdkvnf kfdnjvndsnfjkbgad;skfnd cdsahbfdsc khbdkvc ndjsacnjkdasbc;nxajkednbfaskjbdcnm dsjk;fnvdjkfbf",
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

                //Test of base
                CardTest("2 3", "5")
                CardTest("2 3", "5")
                CardTest("2 3", "5")
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

@Preview
@Composable
fun TaskInfoScreenPreview() {
    TaskInfoScreen(
        "", {},{}
    )
}