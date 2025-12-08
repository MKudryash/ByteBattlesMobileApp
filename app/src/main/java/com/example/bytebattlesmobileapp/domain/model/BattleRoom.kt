package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class BattleRoom(
    val id: UUID,
    val name: String,
    val languageId: UUID,
    val difficulty: BattleDifficulty,
    val hostId: String,
    val participants: List<BattleParticipant>,
    val status: BattleStatus,
    val createdAt: String
)