package com.diva.models.session

enum class SessionStatus {
    ACTIVE,
    EXPIRED,
    CLOSED,
}

fun safeSessionStatus(value: String): SessionStatus {
    return try {
        SessionStatus.valueOf(value)
    } catch (_: IllegalArgumentException) {
        SessionStatus.EXPIRED
    }
}
