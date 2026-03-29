package com.diva.auth.signIn.presentation.events

import com.diva.ui.models.SocialProvider
import com.diva.ui.navigation.arguments.ForgotAction

sealed interface SignInEvents {
    /* Input */
    data class OnUsernameChanged(val email: String) : SignInEvents
    data class OnPasswordChanged(val password: String) : SignInEvents
    data object TogglePassword : SignInEvents

    /* Navigation */
    data class OnNavigateToForgot(val action: ForgotAction) : SignInEvents
    data object OnNavigateToSignUp : SignInEvents

    /* Actions */
    data class OnSocialLogin(val provider: SocialProvider) : SignInEvents
    data object OnSubmit : SignInEvents
}
