package com.diva.models.permission

import com.diva.models.roles.Role
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class Permission(
    val id: Uuid,
    val name: String,
    val description: String,
    val roleLevel: Role,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant> = Option.None,
)
