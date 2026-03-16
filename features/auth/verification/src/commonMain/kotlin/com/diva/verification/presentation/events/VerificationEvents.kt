package com.diva.verification.presentation.events

sealed interface VerificationEvents {
    data object OnBack : VerificationEvents
    data class OnTokenChanged(val token: String) : VerificationEvents
    data object OnSubmit : VerificationEvents
}
