package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun CardNewsOrTask(
    nameOfNews: String,
    date: String,
    description: String,
    onNavigateToTaskInfo: ()->Unit
) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(
                onClick = {
                onNavigateToTaskInfo()
            })
            .padding(10.dp)
            .background(Color(0xFFF0F4F8), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
    )
    {
        Column(modifier = Modifier.Companion.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(0.6f),
                    text =nameOfNews,
                    color = Color.Companion.Black,
                    fontWeight = FontWeight.Companion.Normal,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                )
                Spacer(modifier = Modifier.Companion.width(10.dp))
                Text(
                    modifier = Modifier.weight(0.4f),
                    text = date,
                    textAlign = TextAlign.End,
                    color = Color.Companion.LightGray,
                    fontWeight = FontWeight.Companion.Thin,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),

                    )
            }
            Spacer(modifier = Modifier.Companion.height(10.dp))
            Text(
                text = description,
                color = Color.Companion.DarkGray,
                fontWeight = FontWeight.Companion.Normal,
                fontSize = 13.sp,
                maxLines = 4,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_light)),
            )
        }
    }
}
@Preview
@Composable
fun CardNewsOrTaskPreview(){
    CardNewsOrTask("text", "date", "sdfnksjdfbgljhdsblgdhfblgjhbfd", {})
}