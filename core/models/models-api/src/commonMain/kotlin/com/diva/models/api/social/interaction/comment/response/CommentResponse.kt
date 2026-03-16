package com.diva.models.api.social.interaction.comment.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("interaction_id")
    val interactionId: String,
    @SerialName("reply_to_id")
    val replyToId: String? = null,
    @SerialName("content")
    val content: String,
    @SerialName("edited_at")
    val editedAt: Long? = null
)