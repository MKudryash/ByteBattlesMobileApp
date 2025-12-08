package com.example.bytebattlesmobileapp.data.network.dto.auth

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class JoinRoomRequest(
    @Contextual
    val roomId: String
)