package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun CustomInfoDialog(
    showDialog: Boolean,
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onNavigateToInfo: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {

                Text(
                    modifier = Modifier.Companion.fillMaxWidth().padding(horizontal = 5.dp),
                    textAlign = TextAlign.Companion.Center,
                    text = title,
                    color = Color.Companion.White,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    fontSize = 20.sp,
                    lineHeight = 22.sp
                )
            },
            text = {

                Text(
                    modifier = Modifier.Companion.fillMaxWidth().padding(horizontal = 5.dp),
                    textAlign = TextAlign.Companion.Center,
                    text = text,
                    color = Color.Companion.White,
                    fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.Companion.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Companion.White
                        )

                    ) {
                        Text(
                            text = "Отмена",
                            fontSize = 16.sp,
                            color = Color(0xFF2C3646),
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
                        )
                    }

                    Spacer(modifier = Modifier.Companion.width(8.dp))

                    Button(
                        onClick = {onNavigateToInfo()
                                  onDismiss()},
                        modifier = Modifier.Companion.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF53C2C3)
                        )
                    ) {
                        Text(
                            text = "Да",
                            fontSize = 16.sp,
                            color = Color(0xFF2C3646),
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular))
                        )
                    }
                }
            },
            modifier = Modifier.Companion.fillMaxWidth(),
            containerColor = Color(0xFF3A4752),
            titleContentColor = Color.Companion.White
        )
    }
}