package com.diva.models.api.social.interaction.comment.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentDto(
    @SerialName("interaction_id")
    val interactionId: String,
    @SerialName("content")
    val content: String,
    @SerialName("reply_to_id")
    val replyToId: String? = null
)
