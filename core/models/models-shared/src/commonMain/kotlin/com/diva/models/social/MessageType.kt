package com.diva.models.social

enum class MessageType {
    TEXT,
    MEDIA,
    SYSTEM,
    UNSPECIFIED,
}

fun safeMessageType(value: String): MessageType {
    return try {
        MessageType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        MessageType.UNSPECIFIED
    }
}
