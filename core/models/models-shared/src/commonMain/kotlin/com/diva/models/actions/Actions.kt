package com.diva.models.actions

enum class Actions {
    USER_VERIFICATION,
    PASSWORD_RESET,
    UNKNOWN
}

fun safeActionsValueOf(value: String): Actions {
    return try {
        Actions.valueOf(value)
    } catch (_: IllegalArgumentException) {
        Actions.UNKNOWN
    }
}
