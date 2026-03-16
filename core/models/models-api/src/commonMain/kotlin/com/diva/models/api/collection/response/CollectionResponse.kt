package com.diva.models.api.collection.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponse(
    @SerialName("id")
    val id: String,
    @SerialName("owner_id")
    val ownerId: String,
    @SerialName("cover_media_id")
    val coverMediaId: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("collection_type")
    val collectionType: String,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
    @SerialName("deleted_at")
    val deletedAt: Long? = null,
)


@Serializable
data class CollectionMediaResponse(
    @SerialName("media_id")
    val mediaId: String,
    @SerialName("position")
    val position: Int,
    @SerialName("added_by_id")
    val addedById: String,
    @SerialName("score")
    val score: Float,
    @SerialName("added_at")
    val addedAt: Long,
)
