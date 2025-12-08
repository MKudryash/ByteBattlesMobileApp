package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BattleResultDto(
    @Contextual
    val battleId: UUID,
    val winnerId: String?,
    val participants: List<BattleParticipantResultDto>,
    @Contextual
    val taskId: UUID,
    val startedAt: String,
    val finishedAt: String,
    val duration: Long
)