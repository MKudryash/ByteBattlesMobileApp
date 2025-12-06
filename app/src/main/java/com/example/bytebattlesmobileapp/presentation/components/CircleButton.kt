package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(
    icon: Painter,
    color: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.Companion.size(48.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.Companion
                .size(48.dp)
                .background(
                    color = color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Companion.Center
        ) {
            Image(
                painter = icon,
                contentDescription = "Icon"
            )
        }
    }
}