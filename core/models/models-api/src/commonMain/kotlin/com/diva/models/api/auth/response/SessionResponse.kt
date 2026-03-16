package com.diva.models.api.auth.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("status")
    val status: String,
    @SerialName("device")
    val device: String,
    @SerialName("ip")
    val ip: String = "",
    @SerialName("agent")
    val agent: String,
    @SerialName("expires_at")
    val expiresAt: Long,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
)
