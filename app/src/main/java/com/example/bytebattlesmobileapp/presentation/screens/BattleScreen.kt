package com.example.bytebattlesmobileapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R
import com.example.bytebattlesmobileapp.presentation.components.ActionButton
import com.example.bytebattlesmobileapp.presentation.components.CardLanguage
import com.example.bytebattlesmobileapp.presentation.components.Header
import com.example.bytebattlesmobileapp.presentation.components.TaskCard
import com.example.bytebattlesmobileapp.presentation.components.TopAppBarComponent

@Composable
fun BattleScreen(
    onNavigateBack: () -> Unit,
    onNavigateTrain: () -> Unit,
    onNavigateLobby:()->Unit
) {
    var selectedLanguage by remember { mutableStateOf(0) }
    var selectedTypeBattle by remember { mutableStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3646))
    ) {
        Header({ onNavigateBack() }, "Battle")
        Spacer(Modifier.height(20.dp))
        LazyColumn {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Text(
                        "Язык программирования",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),

                        ) {
                        itemsIndexed(listOf("Python", "Java", "Kotlin", "C++")) { index, language ->

                            CardLanguage(
                                painter = painterResource(R.drawable.csharp),
                                language,
                                selected = selectedLanguage == index,
                                onClick = { selectedLanguage = index }
                            )

                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Уровень сложности",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    Spacer(Modifier.height(12.dp))
                    val typeOfDifficulty = listOf(
                        TypeOf("Easy", R.drawable.easy, 1),
                        TypeOf("Middle", R.drawable.middle, 2),
                        TypeOf("Hard", R.drawable.hard, 3),
                    )
                    LazyRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,

                        ) {
                        items(typeOfDifficulty) { it ->

                            CardLanguage(
                                painter = painterResource(it.iconRes),
                                nameLanguage = it.name,
                                selected = selectedDifficulty == it.id,
                                onClick = { selectedDifficulty = it.id }
                            )

                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Тип битвы",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_semibold)),
                    )
                    Spacer(Modifier.height(12.dp))
                    val typeOfBattle = listOf(
                        TypeOf("1 vs 1", R.drawable.one_vs_one, 1),
                        TypeOf("Командный", R.drawable.command, 2),
                        TypeOf("Турнир", R.drawable.champ, 3),
                    )
                    LazyRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,

                        ) {
                        items(typeOfBattle) { it ->

                            CardLanguage(
                                painter = painterResource(it.iconRes),
                                nameLanguage = it.name,
                                selected = selectedTypeBattle == it.id,
                                onClick = { selectedTypeBattle = it.id }
                            )

                        }
                    }
                    Spacer(Modifier.height(80.dp))
                    Column(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ActionButton(
                            text = "Начать бой".uppercase(),
                            onClick = { onNavigateLobby() },
                            color = Color(0xFF5EC2C3),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        )
                    }
                }
            }
        }
    }

}

data class TypeOf(
    val name: String,
    val iconRes: Int,
    val id: Int
)

@Preview
@Composable
fun BattleScreenPreview() {
    BattleScreen({}, {},{})
}