package com.diva.models.collection

import com.diva.models.media.Media
import com.diva.models.user.User

data class CollectionMedia(
    val media: Media,
    val position: Int,
    val addedBy: User,
    val score: Float,
    val addedAt: Long,
)
