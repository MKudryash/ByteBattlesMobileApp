package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
fun RowProfileStatisticPoint(
    statisticPoints: List<StatisticPoint>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 5.dp), // такой же padding как у элементов
        ) {
            val style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
            )
            Text(
                modifier = Modifier.weight(0.2f), // СОВПАДАЕТ с первым столбцом
                text = "Дата",
                style = style

            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                modifier = Modifier.weight(0.4f), // СОВПАДАЕТ со вторым столбцом
                text = "Задача",
                style = style
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                modifier = Modifier.weight(0.15f), // СОВПАДАЕТ с третьим столбцом
                text = "Тип",
                style = style
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                modifier = Modifier.weight(0.25f),
                text = "Начислено",
                style = style
            )
        }

        // Список элементов
        LazyColumn {
            items(statisticPoints) { point ->
                RowStatisticItem(point = point)
            }
        }
    }
}

@Composable
fun RowStatisticItem(
    point: StatisticPoint,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color =
                if (point.isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
        )
        // Дата
        Text(
            text = point.data,
            style = style,
            modifier = Modifier.weight(0.2f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Задача
        Text(
            text = point.taskName,
            style = style,
            modifier = Modifier.weight(0.4f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Тип
        Text(
            text = point.typeMode,
            style = style,
            modifier = Modifier.weight(0.25f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Очки
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.15f)
        ) {
            Text(
                text = if (point.point > 0) "+${point.point}" else point.point.toString(),
                fontSize = 13.sp,
                color = if (point.isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview
@Composable
fun RowProfileStatisticPointPreview() {
    RowProfileStatisticPoint(
        listOf(StatisticPoint(
            "12.12.1990", "Summa", "Turnir",
            10, true
        ),  StatisticPoint(
            "12.12.1990", "Summa", "Turnir",
            0, false
        ))
    )
}