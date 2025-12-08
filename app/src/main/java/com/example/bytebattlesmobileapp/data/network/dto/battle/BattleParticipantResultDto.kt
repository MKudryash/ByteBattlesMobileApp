package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BattleParticipantResultDto(
    val userId: String,
    val username: String,
    val score: Int,
    @Contextual
    val submissionId: UUID?,
    val completionTime: Long?
)