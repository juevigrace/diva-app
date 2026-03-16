package com.diva.models

enum class VisibilityType {
    PUBLIC,
    PRIVATE,
    FRIENDS,
    UNSPECIFIED,
}

fun safeVisibilityType(value: String): VisibilityType {
    return try {
        VisibilityType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        VisibilityType.UNSPECIFIED
    }
}
