package com.diva.auth.signUp.presentation.state

import com.diva.auth.signUp.data.validation.SignUpValidation
import com.diva.models.auth.SignUpForm

data class SignUpState(
    val signUpForm: SignUpForm = SignUpForm(),
    val formValidation: SignUpValidation = SignUpValidation(),

    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,

    val submitLoading: Boolean = false,
    val submitEnabled: Boolean = false,
    val submitSuccess: Boolean = false,
)
