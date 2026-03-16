package com.diva.models.api.user.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(
    @SerialName("email")
    val email: String,
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    @SerialName("birthDate")
    val birthDate: Long,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("alias")
    val alias: String = username,
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("bio")
    val bio: String = "",
)
