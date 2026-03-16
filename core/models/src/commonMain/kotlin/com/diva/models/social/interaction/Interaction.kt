package com.diva.models.social.interaction

import com.diva.models.social.ReactionType
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Interaction(
    val id: Uuid,
    val user: User,
    val reactionType: ReactionType,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Option<Long> = Option.None,
)
