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
    @SerialName("birth_date")
    val birthDate: Long,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("alias")
    val alias: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bio")
    val bio: String,
    @SerialName("user_verified")
    val userVerified: Boolean,
    @SerialName("role")
    val role: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null
)
