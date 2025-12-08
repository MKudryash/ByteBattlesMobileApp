package com.example.bytebattlesmobileapp.data.network.dto.auth

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: String? = null,
    val user: UserAuth,

)

@Serializable
data class UserAuth(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role
)
@Serializable
data class Role(
    val id: String,
    val name: String,
    val description: String,
)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        val value = decoder.decodeString()
        return try {
            UUID.fromString(value)
        } catch (e: IllegalArgumentException) {
            // Если строка не является валидным UUID, попробуем другие форматы
            if (value.length == 36 && value.contains('-')) {
                UUID.fromString(value)
            } else {
                // Создаем UUID из хэша строки
                UUID.nameUUIDFromBytes(value.toByteArray())
            }
        }
    }
}