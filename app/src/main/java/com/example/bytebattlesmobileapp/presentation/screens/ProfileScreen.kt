package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CustomUnderlinedTextField
import com.example.bytebattlesmobileapp.presentation.components.RememberMeCheckbox
import com.example.bytebattlesmobileapp.presentation.components.SingleRoundedCornerBox
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        // Первый элемент Column - Box
        Box {
            val roundedShape = RoundedCornerShape(15.dp)
            SingleRoundedCornerBox(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.25f),
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 55.dp,
                bottomEnd = 55.dp,
                {
                    Row(horizontalArrangement = Arrangement.SpaceEvenly)
                    {
                       /* Image()

                        Column {
                            Image()
                            Text("${name} ${surname}")
                        }
                        Image()*/
                    }
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            CustomUnderlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = "Иван",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = surname.value,
                onValueChange = { surname.value = it },
                placeholder = "Петров",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Почта",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomUnderlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                placeholder = "Описание",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
            )




            Spacer(modifier = Modifier.height(40.dp))

            ActionButton(
                text = "Сохранить".uppercase(),
                onClick = { },
                color = Color(0xFF5EC2C3),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        {}
    )
}