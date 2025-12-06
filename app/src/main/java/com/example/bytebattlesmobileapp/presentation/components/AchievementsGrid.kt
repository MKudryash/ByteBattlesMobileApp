package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.presentation.screens.Achievement

@Composable
fun AchievementsGrid(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier.Companion
) {
    // Разбиваем список на группы по 3 элемента
    val groupedAchievements = achievements.chunked(3)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(groupedAchievements) { index, group ->
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                group.forEach { achievement ->
                    CardAchievements(
                        painter = painterResource(achievement.iconRes),
                        achievement.title,
                        modifier = Modifier.Companion.weight(1f)
                    )
                }

                repeat(3 - group.size) {
                    Spacer(modifier = Modifier.Companion.weight(1f))
                }
            }

            Spacer(Modifier.Companion.height(5.dp))

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color = Color(0xFF5EC2C3),)
            )
            Spacer(Modifier.Companion.height(15.dp))

        }
    }
}