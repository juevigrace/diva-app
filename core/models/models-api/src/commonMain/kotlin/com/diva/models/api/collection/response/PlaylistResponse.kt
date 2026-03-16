package com.diva.models.api.collection.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("is_collaborative")
    val isCollaborative: Boolean,
    @SerialName("allow_suggestions")
    val allowSuggestions: Boolean,
    @SerialName("contributors")
    val contributors: List<PlaylistContributorResponse> = emptyList(),
    @SerialName("suggestions")
    val suggestions: List<PlaylistSuggestionResponse> = emptyList()
)

@Serializable
data class PlaylistSuggestionResponse(
    @SerialName("id")
    val id: String,
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("suggester_id")
    val suggesterId: String,
    @SerialName("media_id")
    val mediaId: String,
    @SerialName("status")
    val status: String,
    @SerialName("suggested_at")
    val suggestedAt: String
)

@Serializable
data class PlaylistContributorResponse(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("contributor_id")
    val contributorId: String
)
