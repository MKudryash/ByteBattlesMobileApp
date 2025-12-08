package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class BattleResult(
    val battleId: UUID,
    val winnerId: String?,
    val participants: List<BattleParticipantResult>,
    val taskId: UUID,
    val startedAt: String,
    val finishedAt: String,
    val duration: Long
)