package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Serializable

@Serializable
data class BattleParticipantDto(
    val id: String,
    val username: String,
    val isReady: Boolean,
    val isHost: Boolean,
    val joinedAt: String
)

