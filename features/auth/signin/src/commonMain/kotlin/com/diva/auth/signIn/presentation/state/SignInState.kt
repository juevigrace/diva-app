package com.diva.auth.signIn.presentation.state

import com.diva.auth.signIn.data.validation.SignInValidation
import com.diva.models.auth.SignInForm
import com.diva.ui.models.SocialProvider

data class SignInState(
    val signInForm: SignInForm = SignInForm(),
    val formValidation: SignInValidation = SignInValidation(),

    val socialProviders: List<SocialProvider> = SocialProvider.defaultProviders,
    val showPassword: Boolean = false,

    val submitLoading: Boolean = false,
    val submitEnabled: Boolean = false,
    val submitSuccess: Boolean = false,
)
