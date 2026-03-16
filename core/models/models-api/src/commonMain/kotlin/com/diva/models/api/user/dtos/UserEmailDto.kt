package com.diva.models.api.user.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEmailDto(
    @SerialName("email")
    val email: String
)
