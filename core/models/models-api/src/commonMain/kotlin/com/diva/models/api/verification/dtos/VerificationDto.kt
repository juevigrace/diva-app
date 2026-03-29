package com.diva.models.api.verification.dtos

import com.diva.models.api.auth.session.dtos.SessionDataDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationDto(
    @SerialName("token")
    val token: String,
    @SerialName("session_data")
    val sessionData: SessionDataDto? = null
)
