package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun UserHeader(
    name: String = "Name",
    painter: Painter?,
    showIcon: Boolean) {

    Box() {
        CustomVectorShape(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f), {
                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.menu_burger),
                        contentDescription = "menu"
                    )
                    Text(
                        "Привет, ${name}!",
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    UserAvatar(
                        initials = name,
                        imagePainter = painter,// или painterResource
                        showIcon = showIcon
                    )

                }
            })
    }
}