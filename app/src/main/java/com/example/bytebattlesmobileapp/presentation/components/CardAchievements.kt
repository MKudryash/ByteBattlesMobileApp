package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
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
import com.example.bytebattlesmobileapp.presentation.screens.Achievement


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
@Composable
fun AchievementsGrid(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    // Разбиваем список на группы по 3 элемента
    val groupedAchievements = achievements.chunked(3)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(groupedAchievements) { index, group ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                group.forEach { achievement ->
                    CardAchievements(
                        painter = painterResource(achievement.iconRes),
                        achievement.title,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(3 - group.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(5.dp))

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background( color = Color(0xFF5EC2C3),)
            )
            Spacer(Modifier.height(15.dp))

        }
    }
}