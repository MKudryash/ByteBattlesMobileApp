package com.example.bytebattlesmobileapp.presentation.viewmodel


import com.example.bytebattlesmobileapp.domain.model.UserProfile
import com.example.bytebattlesmobileapp.domain.model.Achievement
import com.example.bytebattlesmobileapp.domain.model.UserStats

class ProfileScreenData(
    val profile: UserProfile,
    val stats: UserStats?,
    val scoreHistory: List<ScoreHistory>,
    val achievements: List<Achievement>
)
