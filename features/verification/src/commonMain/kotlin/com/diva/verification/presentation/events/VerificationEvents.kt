package com.diva.verification.presentation.events

import com.diva.ui.navigation.arguments.VerificationAction

sealed interface VerificationEvents {
    data object OnBack : VerificationEvents
    data object OnRequest : VerificationEvents
    data class OnSetAction(val action: VerificationAction) : VerificationEvents
    data object OnSubmit : VerificationEvents
    data class OnTokenChanged(val token: String) : VerificationEvents
}
