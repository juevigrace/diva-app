package com.diva.models.api.chat.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("id")
    val id: String,
    @SerialName("sender_id")
    val senderId: String,
    @SerialName("content")
    val content: String,
    @SerialName("type")
    val type: String,
    @SerialName("reply_to_id")
    val replyTo: String? = null,
    @SerialName("edited_at")
    val editedAt: Long? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
    @SerialName("media")
    val media: List<MessageMediaResponse> = emptyList(),
)

@Serializable
data class MessageMediaResponse(
    @SerialName("media_id")
    val mediaId: String,
)
