package com.diva.models.chat

import com.diva.models.api.chat.response.ChatParticipantResponse
import com.diva.models.social.ParticipantRole
import com.diva.models.social.safeParticipantRole
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class ChatParticipant(
    val user: User,
    val role: ParticipantRole,
    val joinedAt: Instant,
    val lastReadAt: Option<Instant> = Option.None,
    val mutedUntil: Option<Instant> = Option.None,
    val addedBy: User,
) {
    companion object {
        fun fromResponse(response: ChatParticipantResponse): ChatParticipant {
            return ChatParticipant(
                user = User(id = Uuid.parse(response.userId)),
                role = safeParticipantRole(response.role),
                joinedAt = Instant.fromEpochMilliseconds(response.joinedAt),
                lastReadAt = Option.of(response.lastReadAt?.let { Instant.fromEpochMilliseconds(it) }),
                mutedUntil = Option.of(response.mutedUntil?.let { Instant.fromEpochMilliseconds(it) }),
                addedBy = User(id = Uuid.parse(response.addedBy)),
            )
        }
    }
}
