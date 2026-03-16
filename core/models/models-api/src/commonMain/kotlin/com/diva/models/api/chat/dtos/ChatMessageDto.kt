package com.diva.models.api.chat.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMessageDto(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("content")
    val content: String,
    @SerialName("type")
    val type: String,
    @SerialName("reply_to_id")
    val replyToId: String? = null,
    @SerialName("media_ids")
    val mediaIds: List<CreateMessageMediaDto> = emptyList(),
)

@Serializable
data class CreateMessageMediaDto(
    @SerialName("media_id")
    val mediaId: String,
)

@Serializable
data class UpdateMessageDto(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("content")
    val content: String,
)

@Serializable
data class DeleteMessageDto(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("chat_id")
    val chatId: String,
)
