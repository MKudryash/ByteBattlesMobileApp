package com.example.bytebattlesmobileapp.domain.model

data class BattleParticipant(
    val id: String,
    val username: String,
    val isReady: Boolean,
    val isHost: Boolean,
    val joinedAt: String
)