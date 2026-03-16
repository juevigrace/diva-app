package com.diva.models.api.collection.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCollectionDto(
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
    @SerialName("collection_media")
    val media: List<CreateCollectionMediaDto> = emptyList()
)

@Serializable
data class CreateCollectionMediaDto(
    @SerialName("media_id")
    val mediaId: String,
    @SerialName("position")
    val position: Int,
    @SerialName("added_by")
    val addedBy: String? = null,
)
