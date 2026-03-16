package com.diva.models.media.tag

import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class Tag(
    val id: Uuid,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant>,
)
