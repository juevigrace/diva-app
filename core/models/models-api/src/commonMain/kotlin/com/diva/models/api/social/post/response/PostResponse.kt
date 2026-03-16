package com.diva.models.api.social.post.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("id")
    val id: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("media_id")
    val mediaId: String? = null,
    @SerialName("collection_id")
    val collectionId: String? = null,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
    @SerialName("attachments")
    val attachments: List<PostAttachmentResponse> = emptyList()
)

@Serializable
data class PostAttachmentResponse(
    @SerialName("id")
    val id: String,
    @SerialName("media_id")
    val mediaId: String? = null,
    @SerialName("collection_id")
    val collectionId: String? = null
)
