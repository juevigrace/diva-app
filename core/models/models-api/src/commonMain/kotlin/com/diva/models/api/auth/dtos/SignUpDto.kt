package com.diva.models.api.auth.dtos

import com.diva.models.api.user.dtos.CreateUserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpDto(
    @SerialName("user")
    val user: CreateUserDto,
    @SerialName("session_data")
    val sessionData: SessionDataDto
)
