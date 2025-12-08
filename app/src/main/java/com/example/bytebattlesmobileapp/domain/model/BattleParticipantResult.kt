package com.example.bytebattlesmobileapp.domain.model

import java.util.UUID

data class BattleParticipantResult(
    val userId: String,
    val username: String,
    val score: Int,
    val submissionId: UUID?,
    val completionTime: Long?
)