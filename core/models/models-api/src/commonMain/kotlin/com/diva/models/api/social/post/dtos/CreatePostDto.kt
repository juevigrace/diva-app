package com.diva.models.api.social.post.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostDto(
    @SerialName("text")
    val text: String,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("attachment")
    val attachment: List<CreatePostAttachment> = emptyList(),
)

@Serializable
data class CreatePostAttachment(
    @SerialName("media_id")
    val mediaId: String? = null,
    @SerialName("collection_id")
    val collectionId: String? = null
)
