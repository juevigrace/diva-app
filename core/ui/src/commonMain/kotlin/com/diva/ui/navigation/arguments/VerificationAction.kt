package com.diva.ui.navigation.arguments

import kotlinx.serialization.Serializable

@Serializable
sealed interface VerificationAction {
    @Serializable
    data object EmailVerification : VerificationAction

    @Serializable
    data object UserVerification : VerificationAction
}
