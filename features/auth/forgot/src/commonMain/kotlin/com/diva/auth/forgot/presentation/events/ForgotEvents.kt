package com.diva.auth.forgot.presentation.events

sealed interface ForgotEvents {
    data class OnEmailChanged(val email: String) : ForgotEvents
    data class OnTokenChanged(val token: String) : ForgotEvents
    data class OnNewPasswordChanged(val password: String) : ForgotEvents
    data class OnConfirmPasswordChanged(val password: String) : ForgotEvents
    data object OnSubmit : ForgotEvents
    data object OnBack : ForgotEvents
    data object OnNavigateToSignIn : ForgotEvents
}
