package com.diva.models.auth

import com.diva.models.api.auth.response.SessionResponse
import com.diva.models.session.SessionStatus
import com.diva.models.session.safeSessionStatus
import com.diva.models.user.User
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Session(
    val id: Uuid,
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val device: String,
    val status: SessionStatus,
    val isCurrent: Boolean = false,
    val ipAddress: String,
    val userAgent: String,
    val expiresAt: Instant,
    val expired: Boolean = expiresAt < Clock.System.now(),
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun toResponse(): SessionResponse {
        return SessionResponse(
            sessionId = id.toString(),
            userId = user.id.toString(),
            accessToken = accessToken,
            refreshToken = refreshToken,
            status = status.name,
            device = device,
            ip = ipAddress,
            agent = userAgent,
            expiresAt = expiresAt.toEpochMilliseconds(),
            createdAt = createdAt.toEpochMilliseconds(),
            updatedAt = updatedAt.toEpochMilliseconds(),
        )
    }

    companion object {
        fun fromResponse(response: SessionResponse): Session {
            return Session(
                id = Uuid.parse(response.sessionId),
                user = User(id = Uuid.parse(response.userId)),
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                device = response.device,
                status = safeSessionStatus(response.status),
                ipAddress = response.ip,
                userAgent = response.agent,
                expiresAt = Instant.fromEpochMilliseconds(response.expiresAt),
                createdAt = Instant.fromEpochMilliseconds(response.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(response.updatedAt),
            )
        }
    }
}
