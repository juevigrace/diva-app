package com.diva.models.api.auth.forgot.password.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordDto(
    @SerialName("new_password")
    val newPassword: String,
)