package com.diva.models.collection.playlist

import com.diva.models.media.Media
import com.diva.models.user.User

data class PlaylistSuggestions(
    val suggester: User,
    val media: Media,
)
