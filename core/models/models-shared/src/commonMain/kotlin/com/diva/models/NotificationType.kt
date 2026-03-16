package com.diva.models

enum class NotificationType {
    LIKE,
    COMMENT,
    REPLY,
    SHARE,
    FOLLOW,
    MENTION,
    UNSPECIFIED,
}

fun safeNotificationType(value: String): NotificationType {
    return try {
        NotificationType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        NotificationType.UNSPECIFIED
    }
}
