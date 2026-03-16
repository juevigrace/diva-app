package com.diva.models.api.collection.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistSuggestionDto(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("media_id")
    val mediaId: String,
)

@Serializable
data class UpdatePlaylistSuggestionDto(
    @SerialName("id")
    val id: String,
    @SerialName("status")
    val status: String,
)
