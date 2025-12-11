package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bytebattlesmobileapp.R

@Composable
fun BackArrow(
    onNavigateBack:()->Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .clickable(
                onClick =
                    {
                        onNavigateBack()
                    }
            )
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_start),
            contentDescription = "Стрелка",
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
        )
    }
}