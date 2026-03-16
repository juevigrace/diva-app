package com.diva.models.api.social.interaction.comment.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCommentDto(
    @SerialName("interaction_id")
    val interactionId: String,
    @SerialName("content")
    val content: String,
)
