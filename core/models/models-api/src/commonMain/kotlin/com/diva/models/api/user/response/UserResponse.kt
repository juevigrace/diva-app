package com.diva.models.api.user.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
    @SerialName("username")
    val username: String,
    @SerialName("birthDate")
    val birthDate: Long,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("alias")
    val alias: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bio")
    val bio: String,
    @SerialName("userVerified")
    val userVerified: Boolean,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("deletedAt")
    val deletedAt: Long? = null
)
