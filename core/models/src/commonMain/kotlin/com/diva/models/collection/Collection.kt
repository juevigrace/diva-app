package com.diva.models.collection

import com.diva.models.VisibilityType
import com.diva.models.media.Media
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Collection(
    val id: Uuid,
    val owner: User,
    val coverMedia: Media,
    val name: String,
    val description: String,
    val collectionType: CollectionType,
    val visibility: VisibilityType,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant> = Option.None,
    val media: List<CollectionMedia> = emptyList(),
)
