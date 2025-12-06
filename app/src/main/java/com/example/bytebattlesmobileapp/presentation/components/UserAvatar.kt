package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    imagePainter: Painter? = null,
    initials: String? = null,
    showIcon: Boolean = false
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when {
            imagePainter != null -> {
                Image(
                    painter = imagePainter,
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            !initials.isNullOrEmpty() -> {
                Text(
                    text = initials.take(2).uppercase(),
                    color = Color(0xFF53C2C3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            showIcon -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color(0xFF53C2C3),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
