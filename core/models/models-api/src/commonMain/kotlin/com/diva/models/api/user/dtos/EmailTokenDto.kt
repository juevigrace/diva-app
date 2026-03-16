package com.diva.models.api.user.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailTokenDto(
    @SerialName("token")
    val token: String,
)
