package com.diva.models.api.media.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMediaDto(
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
    val sensitiveContent: Boolean? = null,
    @SerialName("adult_content")
    val adultContent: Boolean? = null,
    @SerialName("published_at")
    val publishedAt: Long? = null,
    @SerialName("tags")
    val tags: List<String> = emptyList()
)

@Serializable
data class UpdateMediaDto(
    @SerialName("id")
    val id: String,
    @SerialName("alt_text")
    val altText: String? = null,
    @SerialName("visibility")
    val visibility: String? = null,
    @SerialName("sensitive_content")
    val sensitiveContent: Boolean? = null,
    @SerialName("adult_content")
    val adultContent: Boolean? = null,
    @SerialName("published_at")
    val publishedAt: Long? = null,
    @SerialName("tags")
    val tags: List<String> = emptyList()
)

@Serializable
data class DeleteMediaTagDto(
    @SerialName("tag_id")
    val tagId: String,
    @SerialName("media_id")
    val mediaId: String
)
