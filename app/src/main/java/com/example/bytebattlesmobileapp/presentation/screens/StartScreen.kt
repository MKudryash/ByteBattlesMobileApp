package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton

@Composable
fun StartScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        // Полупрозрачный логотип на заднем плане
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        SingleRoundedCornerBox(
            modifier = Modifier.align(Alignment.BottomCenter),
            {
                onNavigateToAuth
            },{onNavigateToRegister}
        )
    }
}

@Composable
fun SingleRoundedCornerBox(
    modifier: Modifier = Modifier,
    onNavigateToAuth: ()-> Unit,
    onNavigateToRegister: ()-> Unit,
) {
    val roundedShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 55.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .clip(roundedShape)
            .background(Color(0xFF5EC2C3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .padding(top = 56.dp),
                text = "Программируй. Соревнуйся. Вдохновляй",
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.monomaniacone_regular)),
            )

            Spacer(modifier = Modifier.height(36.dp))

            ActionButton(
                text = "ВОЙТИ",
                onClick = {onNavigateToAuth },
                color = Color(0xFF2C3646),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End // ← Выравнивание по правому краю
            ) {
                LinkText(
                    modifier = Modifier.padding(),
                    text = "Или создайте аккаунт",
                    onClick = { onNavigateToRegister},
                )
            }

        }


    }
}

@Composable
fun LinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0xFF2196F3)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color(0xff2C3646),
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    Font(R.font.ibmplexmono_semibold)
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painterResource(R.drawable.arrow_end),
                contentDescription = "Стрелка",
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
// Предварительный просмотр
@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    MaterialTheme {
        StartScreen (
            onNavigateToAuth = {},
            onNavigateToRegister = {}
        )
    }
}
