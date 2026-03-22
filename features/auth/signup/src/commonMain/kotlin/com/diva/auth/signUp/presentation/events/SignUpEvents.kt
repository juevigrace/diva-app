package com.diva.auth.signUp.presentation.events

sealed interface SignUpEvents {
    data class OnAliasNameChanged(val value: String) : SignUpEvents
    data class OnEmailChanged(val value: String) : SignUpEvents
    data class OnUsernameChanged(val value: String) : SignUpEvents
    data class OnPasswordChanged(val value: String) : SignUpEvents
    data class OnConfirmPasswordChanged(val value: String) : SignUpEvents
    data object TogglePrivacyPolicy : SignUpEvents
    data object ToggleTerms : SignUpEvents
    data object TogglePasswordVisibility : SignUpEvents
    data object ToggleConfirmPasswordVisibility : SignUpEvents
    data object OnSubmit : SignUpEvents
    data object OnNavigateToSignIn : SignUpEvents
}
