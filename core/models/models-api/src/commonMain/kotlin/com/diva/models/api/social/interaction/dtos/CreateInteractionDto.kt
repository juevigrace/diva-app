package com.diva.models.api.social.interaction.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInteractionDto(
    @SerialName("post_id")
    val postId: String,
    @SerialName("reaction_type")
    val reactionType: String
)
