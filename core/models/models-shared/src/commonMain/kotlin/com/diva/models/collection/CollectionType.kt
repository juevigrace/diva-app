package com.diva.models.collection

enum class CollectionType {
    ALBUM,
    PLAYLIST,
    MIX,
    FAVORITES,
    FEATURED,
    TRENDING,
    UNSPECIFIED,
}

fun safeCollectionType(value: String): CollectionType {
    return try {
        CollectionType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        CollectionType.UNSPECIFIED
    }
}
