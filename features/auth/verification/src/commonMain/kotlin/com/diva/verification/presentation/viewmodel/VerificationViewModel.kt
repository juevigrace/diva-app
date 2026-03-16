package com.diva.verification.presentation.viewmodel

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
import io.github.juevigrace.diva.core.fold
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

class VerificationViewModel(
    private val action: VerificationAction,
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
            submitEnabled = validation.valid() && !state.submitLoading
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    fun onEvent(event: VerificationEvents) {
        when (event) {
            VerificationEvents.OnBack -> navigator.pop()
            is VerificationEvents.OnTokenChanged -> tokenChanged(event.token)
            VerificationEvents.OnSubmit -> submit()
        }
    }

    private fun tokenChanged(value: String) {
        val filteredValue = value.filter { it.isDigit() }.take(6)
        formState.update { state -> state.copy(token = filteredValue) }
        formValidationState.update { state -> state.copy(showTokenError = true) }
    }

    private fun submit() {
        _state.update { state ->
            state.copy(
                submitLoading = true,
                submitEnabled = false,
                submitSuccess = false
            )
        }
        when (action) {
            VerificationAction.EmailVerification -> handleEmailVerification()
            VerificationAction.UserVerification -> handleUserEmailVerification()
        }
    }

    private fun handleEmailVerification() {
        scope.launch {
            repository.verifyUserEmail(formState.value).collect { result ->
                result.fold(
                    onFailure = { err ->
                        toaster.show(err.toToast())
                        _state.update { state ->
                            state.copy(
                                submitLoading = false,
                                submitEnabled = true,
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
    }

    private fun handleUserEmailVerification() {
        scope.launch {
            repository.verifyUserEmail(formState.value).collect { result ->
                result.fold(
                    onFailure = { err ->
                        toaster.show(err.toToast())
                        _state.update { state ->
                            state.copy(
                                submitLoading = false,
                                submitEnabled = true,
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
    }
}
