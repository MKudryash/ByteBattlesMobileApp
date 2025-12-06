package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun CardProfileStatistic(
    textHeader: String,
    textStatic: String
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(textHeader,
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
        )
        Text(textStatic,
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
        )
    }
}

data class StatisticPoint(
    val data: String,
    val taskName: String,
    val typeMode:String,
    val point:Int,
    val isSuccess: Boolean
)
@Preview
@Composable
fun CardProfileStatisticPreview() {
    CardProfileStatistic("✅ Победы", "87 (72.5%)")
}