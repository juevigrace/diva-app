package com.diva.models.api.verification.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestVerificationDto(
    @SerialName("email")
    val email: String,
    @SerialName("action")
    val action: String
)
