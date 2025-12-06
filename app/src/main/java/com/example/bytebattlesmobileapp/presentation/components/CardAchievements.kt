package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R


@Composable
fun CardAchievements(
    painter: Painter,
    title: String,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painter,
            "AchievementsIcon",
            modifier = Modifier.size(50.dp)
        )
        Text(
            title,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
        )
    }
}
@Preview
@Composable
fun CardAchievementsPreview() {
    CardAchievements(painterResource(R.drawable.firstblood),"Первая кровь")
}
