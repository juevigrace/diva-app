package com.diva.models.social.post

import com.diva.models.VisibilityType
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Post(
    val id: Uuid,
    val author: User,
    val text: String,
    val visibility: VisibilityType,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant>,
    val attachments: List<PostAttachment> = emptyList()
)
