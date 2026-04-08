package com.diva.ui.navigation.arguments

import kotlinx.serialization.Serializable

@Serializable
sealed interface VerificationAction {
    @Serializable
    data object UserVerification : VerificationAction

    @Serializable
    data class PasswordReset(val email: String) : VerificationAction

    @Serializable
    data object Unspecified : VerificationAction
}
