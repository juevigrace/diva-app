package com.diva.models.api.media.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    @SerialName("id")
    val id: String,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
)
