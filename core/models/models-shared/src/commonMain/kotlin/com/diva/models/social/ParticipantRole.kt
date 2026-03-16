package com.diva.models.social

enum class ParticipantRole {
    OWNER,
    ADMIN,
    MEMBER,
    UNSPECIFIED,
}

fun safeParticipantRole(value: String): ParticipantRole {
    return try {
        ParticipantRole.valueOf(value)
    } catch (_: IllegalArgumentException) {
        ParticipantRole.UNSPECIFIED
    }
}
