package com.diva.models.chat

import com.diva.models.api.chat.response.ChatResponse
import com.diva.models.media.Media
import com.diva.models.social.ChatType
import com.diva.models.social.safeChatType
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Chat(
    val id: Uuid,
    val createdBy: User,
    val type: ChatType,
    val name: String,
    val description: String = "",
    val avatar: Option<Media> = Option.None,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant> = Option.None,
    val participants: List<ChatParticipant> = emptyList(),
    val messages: List<Message> = emptyList(),
) {

    companion object {
        @OptIn(ExperimentalTime::class)
        fun fromResponse(response: ChatResponse): Chat {
            return Chat(
                id = Uuid.parse(response.id),
                createdBy = User(id = Uuid.parse(response.createdBy)),
                type = safeChatType(response.type),
                name = response.name,
                description = response.description,
                avatar = Option.of(response.avatar?.let { Media(id = Uuid.parse(it)) }),
                createdAt = Instant.fromEpochMilliseconds(response.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(response.updatedAt),
                deletedAt = Option.of(response.deletedAt?.let { Instant.fromEpochMilliseconds(it) }),
            )
        }
    }
}
