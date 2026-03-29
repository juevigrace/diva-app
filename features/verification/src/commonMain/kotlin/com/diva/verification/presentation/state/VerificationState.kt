package com.diva.verification.presentation.state

import com.diva.models.verification.VerificationForm
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.data.validation.VerificationValidation

data class VerificationState(
    val action: VerificationAction = VerificationAction.Unspecified,

    val verificationForm: VerificationForm = VerificationForm(),
    val formValidation: VerificationValidation = VerificationValidation(),

    val submitLoading: Boolean = false,
    val submitEnabled: Boolean = false,
    val submitSuccess: Boolean = false,
)
