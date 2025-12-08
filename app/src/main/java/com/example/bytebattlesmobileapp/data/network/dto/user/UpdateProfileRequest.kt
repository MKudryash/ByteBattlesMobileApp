package com.example.bytebattlesmobileapp.data.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String? = null,
    val country: String? = null,
    val bio:String? = null,
    val gitHubUrl:String? = null
)