package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun CardTest(
    output: String,
    expected: String
) {
    var rounded = 6.dp
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0xFF3A4752), shape = RoundedCornerShape(rounded))
            .border(
                width = 1.dp,
                color = Color(0xFF5EC2C3),
                shape = RoundedCornerShape(rounded)
            ),
    ){
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row {
                Text("Входные параметры: ${output}",
                    color = Color.White,
                    fontWeight = FontWeight.Companion.Normal,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),)
            }
            Spacer(Modifier.height(10.dp))
            Row {
                Text("Выходные параметры: ${expected}",
                    color = Color.White,
                    fontWeight = FontWeight.Companion.Normal,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),)
            }
        }
    }
}

@Preview
@Composable
fun CardTestPreview(){
    CardTest("2 3", "5")
}