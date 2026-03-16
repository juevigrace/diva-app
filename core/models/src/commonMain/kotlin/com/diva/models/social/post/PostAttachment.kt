package com.diva.models.social.post

import com.diva.models.collection.Collection
import com.diva.models.media.Media
import io.github.juevigrace.diva.core.Option
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class PostAttachment(
    val media: Option<Media>,
    val collection: Option<Collection>,
)
