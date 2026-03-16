package com.diva.models.api.collection.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCollectionDto(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("cover_media_id")
    val coverMediaId: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("collection_media")
    val media: List<UpdateCollectionMediaDto> = emptyList()
)

@Serializable
data class UpdateCollectionMediaDto(
    @SerialName("media_id")
    val mediaId: String,
    @SerialName("position")
    val position: Int,
    @SerialName("score")
    val score: Double,
)
