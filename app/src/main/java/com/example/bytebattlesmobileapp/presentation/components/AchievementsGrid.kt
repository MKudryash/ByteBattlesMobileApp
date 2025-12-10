package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.domain.model.Achievement

@Composable
fun AchievementsGrid(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    // Разбиваем список на группы по 3 элемента
    val groupedAchievements = achievements.chunked(3)

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(groupedAchievements) { index, group ->
            // Добавляем разделитель перед всеми элементами, кроме первого
            if (index > 0) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFF5EC2C3))
                    )

            }

            // Контент группы

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp), // Добавляем вертикальные отступы
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    group.forEach { achievement ->
                        CardAchievements(
                            painter = painterResource(R.drawable.firstblood),
                            achievement.name!!,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

        }
    }
}
