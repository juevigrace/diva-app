package com.diva.models.api.social.interaction.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InteractionResponse(
    @SerialName("id")
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("post_id")
    val postId: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null
)
