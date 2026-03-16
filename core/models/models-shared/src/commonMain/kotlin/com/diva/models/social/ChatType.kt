package com.diva.models.social

enum class ChatType {
    DIRECT,
    GROUP,
    UNSPECIFIED,
}

fun safeChatType(value: String): ChatType {
    return try {
        ChatType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        ChatType.UNSPECIFIED
    }
}
