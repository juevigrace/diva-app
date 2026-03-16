package com.diva.models

enum class ModerationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    HIDDEN,
    UNSPECIFIED,
}

fun safeModerationStatus(value: String): ModerationStatus {
    return try {
        ModerationStatus.valueOf(value)
    } catch (_: IllegalArgumentException) {
        ModerationStatus.UNSPECIFIED
    }
}
