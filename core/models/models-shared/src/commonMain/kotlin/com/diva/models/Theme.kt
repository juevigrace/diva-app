package com.diva.models

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM,
}

fun safeValueOfTheme(value: String): Theme {
    return try {
        Theme.valueOf(value)
    } catch (_: IllegalArgumentException) {
        Theme.SYSTEM
    }
}
