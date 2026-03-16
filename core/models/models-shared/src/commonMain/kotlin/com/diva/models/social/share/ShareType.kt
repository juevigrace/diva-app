package com.diva.models.social.share

enum class ShareType {
    DIRECT,
    EXTERNAL,
    EMBED,
    DOWNLOAD,
    UNSPECIFIED,
}

fun safeShareType(value: String): ShareType {
    return try {
        ShareType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        ShareType.UNSPECIFIED
    }
}
