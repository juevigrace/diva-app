package com.diva.models.api.chat.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddParticipantDto(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("role")
    val role: String,
)

@Serializable
data class UpdateParticipantDto(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("role")
    val role: String,
    @SerialName("last_read_at")
    val lastReadAt: Long,
)

@Serializable
data class DeleteParticipantDto(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
)
