package com.diva.ui.navigation.arguments

import kotlinx.serialization.Serializable

@Serializable
sealed interface ForgotAction {
    @Serializable
    data object Password : ForgotAction
}
