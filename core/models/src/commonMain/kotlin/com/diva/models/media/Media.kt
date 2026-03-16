package com.diva.models.media

import com.diva.models.VisibilityType
import com.diva.models.media.tag.Tag
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Media(
    val id: Uuid,
    val submittedBy: User = User(id = Uuid.NIL),
    val url: String = "",
    val altText: String = "",
    val mediaType: MediaType = MediaType.IMAGE,
    val fileSize: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val duration: Int = 0,
    val visibility: VisibilityType = VisibilityType.PUBLIC,
    val sensitiveContent: Boolean = false,
    val adultContent: Boolean = false,
    val publishedAt: Instant = Instant.DISTANT_PAST,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val deletedAt: Option<Instant> = Option.None,
    val tags: List<Tag> = emptyList(),
)
