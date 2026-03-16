package com.diva.ui.models

sealed interface SocialProvider {
    data object Google : SocialProvider
    data object Facebook : SocialProvider
    data object Twitter : SocialProvider

    companion object {
        val defaultProviders = listOf(Google, Facebook, Twitter)
    }
}
