package com.diva.ui.navigation.arguments

import kotlinx.serialization.Serializable

@Serializable
sealed interface PlayerArgs {
    @Serializable
    data class Collection(val collectionId: String) : PlayerArgs

    @Serializable
    data class Media(val mediaId: String) : PlayerArgs
}
