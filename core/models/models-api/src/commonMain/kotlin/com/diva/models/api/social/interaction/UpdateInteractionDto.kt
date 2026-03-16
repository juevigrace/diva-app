package com.diva.models.api.social.interaction.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInteractionDto(
    @SerialName("interaction_type")
    val interactionType: String,
    @SerialName("content")
    val content: String,
)