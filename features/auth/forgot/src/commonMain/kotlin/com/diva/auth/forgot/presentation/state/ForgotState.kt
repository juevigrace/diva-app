package com.diva.auth.forgot.presentation.state

import com.diva.models.auth.EmailForm
import com.diva.models.auth.PasswordResetForm
import com.diva.models.user.actions.UserAction
import com.diva.auth.forgot.data.validation.EmailFormValidation
import com.diva.auth.forgot.data.validation.PasswordResetValidation
import com.diva.ui.navigation.arguments.ForgotAction

data class ForgotState(
    val action: ForgotAction = ForgotAction.Unspecified,
    val userAction: UserAction? = null,
    val step: ForgotStep = ForgotStep.Email,

    val emailForm: EmailForm = EmailForm(),
    val emailFormValidation: EmailFormValidation = EmailFormValidation(),

    val passwordResetForm: PasswordResetForm = PasswordResetForm(),
    val passwordResetValidation: PasswordResetValidation = PasswordResetValidation(),

    val submitLoading: Boolean = false,
    val submitEnabled: Boolean = false,
    val submitSuccess: Boolean = false,
)

enum class ForgotStep {
    Email,
    PasswordReset
}
