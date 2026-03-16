package com.diva.models.chat

import com.diva.models.api.chat.response.MessageResponse
import com.diva.models.media.Media
import com.diva.models.social.MessageType
import com.diva.models.social.safeMessageType
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
data class Message(
    val id: Uuid,
    val sender: User,
    val content: String,
    val type: MessageType,
    val replyTo: Option<Message> = Option.None,
    val editedAt: Option<Instant> = Option.None,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Option<Instant> = Option.None,
    val media: List<Media> = emptyList(),
) {
    companion object {
        fun fromResponse(response: MessageResponse, onReplyMessage: (String) -> Message): Message {
            return Message(
                id = Uuid.parse(response.id),
                sender = User(id = Uuid.parse(response.senderId)),
                content = response.content,
                type = safeMessageType(response.type),
                replyTo = Option.of(response.replyTo?.let { onReplyMessage(it) }),
                editedAt = Option.of(response.editedAt?.let { Instant.fromEpochMilliseconds(it) }),
                createdAt = Instant.fromEpochMilliseconds(response.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(response.updatedAt),
                deletedAt = Option.of(response.deletedAt?.let { Instant.fromEpochMilliseconds(it) }),
            )
        }
    }
}
