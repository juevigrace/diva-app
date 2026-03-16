package com.diva.models.api.chat.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    @SerialName("id")
    val id: String,
    @SerialName("created_by")
    val createdBy: String,
    @SerialName("type")
    val type: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("avatar")
    val avatar: String? = null,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
    @SerialName("participants")
    val participants: List<ChatParticipantResponse> = emptyList(),
    @SerialName("messages")
    val messages: List<MessageResponse> = emptyList(),
)

@Serializable
data class ChatParticipantResponse(
    @SerialName("user_id")
    val userId: String,
    @SerialName("role")
    val role: String,
    @SerialName("joined_at")
    val joinedAt: Long,
    @SerialName("last_read_at")
    val lastReadAt: Long? = null,
    @SerialName("muted_until")
    val mutedUntil: Long? = null,
    @SerialName("added_by")
    val addedBy: String,
)
