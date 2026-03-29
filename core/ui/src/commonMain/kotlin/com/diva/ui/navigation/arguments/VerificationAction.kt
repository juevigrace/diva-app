package com.diva.ui.navigation.arguments

import kotlinx.serialization.Serializable

@Serializable
sealed interface VerificationAction {
    @Serializable
    data object UserVerification : VerificationAction

    @Serializable
    data object PasswordConfirm : VerificationAction

    @Serializable
    data object Unspecified : VerificationAction
}
