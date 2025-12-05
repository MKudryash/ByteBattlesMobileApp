package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

@Composable
fun RememberMeCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кастомный квадратик с галочкой
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isChecked) Color(0xFF5EC2C3) else Color.Transparent
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF5EC2C3),
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                // Галочка (можно использовать вектор или PNG)
               /* Image(
                    painter = painterResource(id = R.drawable.ic_check), // Добавьте иконку галочки
                    contentDescription = "Галочка",
                    modifier = Modifier.size(16.dp)
                )*/

                // Или альтернатива - Text с галочкой
                 Text(
                     text = "✓",
                     color = Color.White,
                     fontSize = 14.sp,
                     fontFamily = FontFamily(
                         Font(R.font.ibmplexmono_regular)
                     )
                 )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.ibmplexmono_regular)
            ),
            lineHeight = 18.sp
        )
    }
}


