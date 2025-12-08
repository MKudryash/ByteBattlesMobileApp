package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BattleRoomResponse(
    @Contextual
    val roomId: UUID,
    val roomName: String,
    @Contextual
    val languageId: UUID,
    val difficulty: String,
    val hostId: String,
    val participants: List<BattleParticipantDto>,
    val status: String, // Waiting, ReadyCheck, Active, Finished
    val createdAt: String
)