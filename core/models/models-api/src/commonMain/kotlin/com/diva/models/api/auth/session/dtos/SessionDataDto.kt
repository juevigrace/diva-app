package com.diva.models.api.auth.session.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDataDto(
    @SerialName("device")
    val device: String,
    @SerialName("user_agent")
    val userAgent: String? = null,
)