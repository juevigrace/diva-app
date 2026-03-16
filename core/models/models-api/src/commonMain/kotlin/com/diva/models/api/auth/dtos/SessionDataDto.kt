package com.diva.models.api.auth.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDataDto(
    @SerialName("device")
    val device: String,
    @SerialName("ip_address")
    val ipAddress: String? = null,
    @SerialName("user_agent")
    val userAgent: String? = null,
)
