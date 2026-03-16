package com.diva.models.collection.playlist

import com.diva.models.collection.Collection
import com.diva.models.user.User

data class Playlist(
    val collection: Collection,
    val isCollaborative: Boolean,
    val allowSuggestions: Boolean,
    val collaborators: List<User> = emptyList(),
    val suggestions: List<PlaylistSuggestions> = emptyList(),
)
