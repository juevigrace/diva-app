package com.diva.auth.forgot.presentation.viewmodel

import com.diva.auth.forgot.data.ForgotRepository
import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.auth.forgot.presentation.state.ForgotStep
import com.diva.models.actions.Actions
import com.diva.models.auth.EmailForm
import com.diva.models.auth.PasswordResetForm
import com.diva.auth.forgot.data.validation.EmailFormValidation
import com.diva.auth.forgot.data.validation.EmailRequestValidator
import com.diva.auth.forgot.data.validation.PasswordResetValidation
import com.diva.auth.forgot.data.validation.PasswordResetValidator
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SignInDestination
import com.diva.ui.navigation.VerificationDestination
import com.diva.ui.navigation.arguments.ForgotAction
import com.diva.ui.navigation.arguments.VerificationAction
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.util.logError
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotViewModel(
    private val repository: ForgotRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster,
) : DivaViewModel() {
    private val emailFormState = MutableStateFlow(EmailForm())
    private val emailFormValidationState = MutableStateFlow(EmailFormValidation())

    private val passwordResetFormState = MutableStateFlow(PasswordResetForm())
    private val passwordResetValidationState = MutableStateFlow(PasswordResetValidation())

    private val combinedEmailFormValidation: StateFlow<EmailFormValidation> = combine(
        emailFormState,
        emailFormValidationState,
    ) { form, validation ->
        val valid = EmailRequestValidator.validate(form)
        validation.copy(
            emailError = if (validation.showEmailError) valid.emailError else Option.None,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emailFormValidationState.value
    )

    private val combinedPasswordResetValidation: StateFlow<PasswordResetValidation> = combine(
        passwordResetFormState,
        passwordResetValidationState,
    ) { form, validation ->
        val valid = PasswordResetValidator.validate(form)
        validation.copy(
            passwordError = if (validation.showPasswordError) valid.passwordError else Option.None,
            confirmPasswordError = if (validation.showConfirmPasswordError) valid.confirmPasswordError else Option.None,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = passwordResetValidationState.value
    )

    private val _state: MutableStateFlow<ForgotState> = MutableStateFlow(ForgotState())
    val state: StateFlow<ForgotState> = combine(
        _state,
        emailFormState,
        combinedEmailFormValidation,
        passwordResetFormState,
        combinedPasswordResetValidation,
    ) { state, emailForm, emailValidation, passwordForm, passwordValidation ->
        println(state.step)
        println(emailValidation.valid())
        println(emailValidation.valid() && !state.submitLoading)
        state.copy(
            emailForm = emailForm,
            emailFormValidation = emailValidation,
            passwordResetForm = passwordForm,
            passwordResetValidation = passwordValidation,
            submitEnabled = when (state.step) {
                ForgotStep.Email -> {
                    emailValidation.valid() && !state.submitLoading
                }
                ForgotStep.PasswordReset -> {
                    passwordValidation.valid() && !state.submitLoading
                }
            },
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    fun onEvent(event: ForgotEvents) {
        when (event) {
            is ForgotEvents.SetAction -> setAction(event.action)
            ForgotEvents.OnCheckAction -> checkAction()
            is ForgotEvents.OnEmailChanged -> emailChanged(event.email)
            is ForgotEvents.OnNewPasswordChanged -> newPasswordChanged(event.password)
            is ForgotEvents.OnConfirmPasswordChanged -> confirmPasswordChanged(event.password)
            ForgotEvents.OnSubmit -> handleOnSubmit()
            ForgotEvents.OnBack -> navigator.pop()
            ForgotEvents.OnNavigateToSignIn -> navigator.replaceAll(SignInDestination)
        }
    }

    private fun setAction(action: ForgotAction) {
        _state.update { state -> state.copy(action = action) }
    }

    private fun checkAction() {
        scope.launch {
            repository.checkForAction(_state.value.action).fold(
                onFailure = { err ->
                    if (err is ConstraintException && err.field == "session") {
                        return@fold
                    }
                    logError(this::class.simpleName ?: "ForgotViewModel", err.toString())
                    toaster.show(err.toToast())
                },
                onSuccess = { action ->
                    _state.update { state ->
                        state.copy(
                            userAction = action,
                        )
                    }
                }
            )
            updateForgotStep()
        }
    }

    private fun updateForgotStep() {
        _state.update { state ->
            state.copy(
                step = state.userAction?.let { action ->
                    when (action.action) {
                        Actions.PASSWORD_RESET -> ForgotStep.PasswordReset
                        else -> null
                    }
                } ?: ForgotStep.Email
            )
        }
    }

    private fun emailChanged(value: String) {
        emailFormState.update { state -> state.copy(email = value) }
        emailFormValidationState.update { state -> state.copy(showEmailError = true) }
    }

    private fun newPasswordChanged(value: String) {
        passwordResetFormState.update { state -> state.copy(newPassword = value) }
        passwordResetValidationState.update { state -> state.copy(showPasswordError = true) }
    }

    private fun confirmPasswordChanged(value: String) {
        passwordResetFormState.update { state -> state.copy(confirmPassword = value) }
        passwordResetValidationState.update { state -> state.copy(showConfirmPasswordError = true) }
    }

    private fun handleOnSubmit() {
        _state.update { state ->
            state.copy(
                submitLoading = true,
                submitEnabled = false,
                submitSuccess = false
            )
        }
        when (_state.value.step) {
            ForgotStep.Email -> handleSendVerificationCode()
            ForgotStep.PasswordReset -> handlePasswordReset()
        }
    }

    private fun handleSendVerificationCode() {
        navigator.navigate(VerificationDestination(VerificationAction.PasswordReset(emailFormState.value.email)))
        _state.update { state -> state.copy(submitLoading = false) }
    }

    private fun handlePasswordReset() {
        scope.launch {
            repository.forgotPasswordReset(passwordResetFormState.value.newPassword).fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "ForgotViewModel", err.toString())
                    toaster.show(err.toToast())
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = false,
                            submitEnabled = true
                        )
                    }
                },
                onSuccess = {
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = true,
                            submitEnabled = true
                        )
                    }
                    navigator.replaceAll(SignInDestination)
                }
            )
        }
    }
}
