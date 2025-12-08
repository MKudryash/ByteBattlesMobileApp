package com.example.bytebattlesmobileapp.data.network.dto.battle

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BattleRoomRequest(
    val roomName: String,
    @Contextual
    val languageId: UUID,
    val difficulty: String // Easy, Medium, Hard
)