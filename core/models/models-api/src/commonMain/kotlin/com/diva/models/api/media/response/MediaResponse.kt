package com.diva.models.api.media.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    @SerialName("id")
    val id: String,
    @SerialName("submitted_by_id")
    val submittedById: String,
    @SerialName("url")
    val url: String,
    @SerialName("alt_text")
    val altText: String,
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("file_size")
    val fileSize: Long,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("duration")
    val duration: Int,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("sensitive_content")
    val sensitiveContent: Boolean,
    @SerialName("adult_content")
    val adultContent: Boolean,
    @SerialName("published_at")
    val publishedAt: Long,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
    @SerialName("tags")
    val tags: List<MediaTagResponse> = emptyList()
)

@Serializable
data class MediaTagResponse(
    @SerialName("tag_id")
    val tagId: String
)
