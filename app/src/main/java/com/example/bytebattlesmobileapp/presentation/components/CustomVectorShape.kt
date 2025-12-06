package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


// Упрощенная версия для лучшего понимания
@Composable
fun CustomVectorShape(
    modifier: Modifier = Modifier,
    content:@Composable ()->Unit
) {
    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {

                moveTo(0f, 0f)


                lineTo(size.width, 0f)


                lineTo(size.width, size.height * 0.95f) // Немного ниже


                cubicTo(
                    size.width * 0.99f, size.height * 0.87f, // контрольная точка 1
                    size.width * 0.96f, size.height * 0.76f, // контрольная точка 2
                    size.width * 0.91f, size.height * 0.76f  // конечная точка 1
                )


                lineTo(size.width * 0.093f, size.height * 0.76f) // 40/428 ≈ 0.093


                cubicTo(
                    size.width * 0.042f, size.height * 0.76f, // 17.91/428 ≈ 0.042
                    size.width * 0f, size.height * 0.64f,     // 98.03/153 ≈ 0.64
                    size.width * 0f, size.height * 0.5f       // 75.94/153 ≈ 0.5
                )


                lineTo(0f, 0f)

                close()
            }

            drawPath(
                path = path,
                color = Color(0xFF53C2C3)
            )

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun VectorPreview() {
    CustomVectorShape(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f),{}) // упрощенная и понятная версия
}