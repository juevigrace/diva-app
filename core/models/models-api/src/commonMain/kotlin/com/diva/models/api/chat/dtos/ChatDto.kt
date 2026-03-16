package com.diva.models.api.chat.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatDto(
    @SerialName("type")
    val type: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("avatar")
    val avatar: String? = null,
)

@Serializable
data class UpdateChatDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("avatar")
    val avatar: String? = null,
)
