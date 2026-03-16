package com.diva.models.social

enum class ReactionType {
    LIKE,
    COMMENT,
    BOOKMARK,
    SHARE,
    UNSPECIFIED,
}

fun safeReactionType(value: String): ReactionType {
    return try {
        ReactionType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        ReactionType.UNSPECIFIED
    }
}
