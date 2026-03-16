package com.diva.models.api.collection.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteCollectionMedia(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("media_id")
    val mediaId: String
)
