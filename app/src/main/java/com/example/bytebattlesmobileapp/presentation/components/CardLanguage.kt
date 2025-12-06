package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun CardLanguage(
    painter: Painter,
    nameLanguage: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFF53C2C3) else Color.Transparent
    val borderWidth = if (selected) 3.dp else 0.dp

    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(if (selected) 2.dp else 0.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "languageIcon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = nameLanguage,
            modifier = Modifier.width(70.dp),
            color = if (selected) Color(0xFF53C2C3) else Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
@Preview
@Composable
fun CardLanguagePreview()
{
    /*CardLanguage()*/
}
