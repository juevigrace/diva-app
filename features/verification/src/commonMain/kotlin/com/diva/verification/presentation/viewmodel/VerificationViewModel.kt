package com.diva.verification.presentation.viewmodel

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.email_sent
import com.diva.core.ui.resources.error_action_not_specified
import com.diva.models.actions.Actions
import com.diva.models.verification.VerificationForm
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.data.VerificationRepository
import com.diva.verification.data.validation.VerificationValidation
import com.diva.verification.data.validation.VerificationValidator
import com.diva.verification.presentation.events.VerificationEvents
import com.diva.verification.presentation.state.VerificationState
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.util.logError
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.ToastMessage
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class VerificationViewModel(
    private val repository: VerificationRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster,
) : DivaViewModel() {
    private val formState = MutableStateFlow(VerificationForm())

    private val formValidationState = MutableStateFlow(VerificationValidation())

    private val combinedValidationState: StateFlow<VerificationValidation> = combine(
        formState,
        formValidationState,
    ) { form, validation ->
        val valid = VerificationValidator.validate(form)
        validation.copy(
            tokenError = if (validation.showTokenError) valid.tokenError else Option.None,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = formValidationState.value
    )

    private val _state: MutableStateFlow<VerificationState> = MutableStateFlow(VerificationState())
    val state: StateFlow<VerificationState> = combine(
        _state,
        formState,
        combinedValidationState,
    ) { state, form, validation ->
        state.copy(
            verificationForm = form,
            formValidation = validation,
            submitEnabled = (validation.valid() && !state.submitLoading) &&
                state.action !is VerificationAction.Unspecified,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    fun onEvent(event: VerificationEvents) {
        when (event) {
            is VerificationEvents.OnRender -> init(event.action)
            is VerificationEvents.OnRequest -> requestVerification()
            VerificationEvents.OnSubmit -> submit()
            VerificationEvents.OnBack -> navigator.pop()
            VerificationEvents.OnResendEnable -> enableResend()
            is VerificationEvents.OnTokenChanged -> tokenChanged(event.token)
        }
    }

    private fun requestVerification() {
        _state.update { state -> state.copy(resendEnabled = false, resendCountdown = 60) }
        when (val action = _state.value.action) {
            is VerificationAction.PasswordReset -> handleRequestPasswordReset(action.email)
            VerificationAction.UserVerification -> handleRequestUserVerification()
            VerificationAction.Unspecified -> {
                scope.launch {
                    toaster.show(
                        ToastMessage(
                            message = getString(Res.string.error_action_not_specified),
                            isError = true
                        )
                    )
                }
            }
        }
    }

    private fun handleRequestPasswordReset(email: String) {
        scope.launch {
            repository.requestPasswordReset(email).fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "VerificationViewModel", err.toString())
                    toaster.show(err.toToast())
                },
                onSuccess = {
                    toaster.show(ToastMessage(message = getString(Res.string.email_sent)))
                }
            )
            startResendCountDown()
        }
    }

    private fun handleRequestUserVerification() {
        scope.launch {
            repository.requestUserVerification().fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "VerificationViewModel", err.toString())
                    toaster.show(err.toToast())
                },
                onSuccess = {
                    toaster.show(ToastMessage(message = getString(Res.string.email_sent)))
                }
            )
            startResendCountDown()
        }
    }

    private fun submit() {
        _state.update { state ->
            state.copy(
                submitLoading = true,
                submitSuccess = false
            )
        }
        when (_state.value.action) {
            is VerificationAction.PasswordReset -> handlePasswordConfirm()
            VerificationAction.UserVerification -> handleUserEmailVerification()
            VerificationAction.Unspecified -> {
                scope.launch {
                    toaster.show(
                        ToastMessage(
                            message = getString(Res.string.error_action_not_specified),
                            isError = true
                        )
                    )
                }
            }
        }
    }

    private fun handlePasswordConfirm() {
        scope.launch {
            repository.verify(formState.value.token, Actions.PASSWORD_RESET).fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "VerificationViewModel", err.toString())
                    toaster.show(err.toToast())
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = false,
                        )
                    }
                },
                onSuccess = {
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = true
                        )
                    }
                    navigator.pop()
                }
            )
        }
    }

    private fun handleUserEmailVerification() {
        scope.launch {
            repository.verify(formState.value.token, Actions.USER_VERIFICATION).fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "VerificationViewModel", err.toString())
                    toaster.show(err.toToast())
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = false,
                        )
                    }
                },
                onSuccess = {
                    _state.update { state ->
                        state.copy(
                            submitLoading = false,
                            submitSuccess = true
                        )
                    }
                    navigator.replaceAll(HomeDestination)
                }
            )
        }
    }

    private fun init(action: VerificationAction) {
        _state.update { state ->
            state.copy(
                action = action,
            )
        }
    }
    private fun enableResend() {
        _state.update { state -> state.copy(resendEnabled = true) }
    }

    private fun startResendCountDown() {
        scope.launch {
            for (i in 59 downTo 1) {
                _state.update { it.copy(resendCountdown = i) }
                delay(1000)
            }
            _state.update { it.copy(resendEnabled = true, resendCountdown = 0) }
        }
    }

    private fun tokenChanged(value: String) {
        formState.update { state -> state.copy(token = value) }
        formValidationState.update { state -> state.copy(showTokenError = true) }
    }
}
