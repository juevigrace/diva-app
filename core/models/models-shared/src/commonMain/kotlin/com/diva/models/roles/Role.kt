package com.diva.models.roles

enum class Role {
    ADMIN,
    USER,
    MODERATOR,
}

fun safeRole(value: String): Role {
    return try {
        Role.valueOf(value)
    } catch (_: IllegalArgumentException) {
        Role.USER
    }
}
