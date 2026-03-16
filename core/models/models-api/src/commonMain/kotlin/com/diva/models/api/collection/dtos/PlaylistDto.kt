package com.diva.models.api.collection.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("is_collaborative")
    val isCollaborative: Boolean,
    @SerialName("allow_suggestions")
    val allowSuggestions: Boolean,
)

@Serializable
data class PlaylistContributorDto(
    @SerialName("contributor_id")
    val contributorId: String,
    @SerialName("collection_id")
    val collectionId: String
)
