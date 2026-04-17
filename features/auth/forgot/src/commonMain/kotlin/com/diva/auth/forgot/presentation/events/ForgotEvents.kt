package com.diva.auth.forgot.presentation.events

import com.diva.ui.navigation.arguments.ForgotAction

sealed interface ForgotEvents {
    data class OnRender(val action: ForgotAction) : ForgotEvents
    data object OnCheckAction : ForgotEvents
    data class OnEmailChanged(val email: String) : ForgotEvents
    data class OnNewPasswordChanged(val password: String) : ForgotEvents
    data class OnConfirmPasswordChanged(val password: String) : ForgotEvents
    data object OnSubmit : ForgotEvents
    data object OnBack : ForgotEvents
    data object OnNavigateToSignIn : ForgotEvents
}
