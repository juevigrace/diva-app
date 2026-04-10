package com.diva.auth.signUp.presentation.viewmodel

import com.diva.auth.signUp.data.SignUpRepository
import com.diva.auth.signUp.data.validation.SignUpValidation
import com.diva.auth.signUp.data.validation.SignUpValidator
import com.diva.auth.signUp.presentation.events.SignUpEvents
import com.diva.auth.signUp.presentation.state.SignUpState
import com.diva.models.auth.SessionData
import com.diva.models.auth.SignUpForm
import com.diva.models.config.AppConfig
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination
import com.diva.user.data.UserRepository
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: SignUpRepository,
    private val uRepository: UserRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster,
    private val config: AppConfig,
) : DivaViewModel() {
    private val formState = MutableStateFlow(SignUpForm())

    private val formValidationState = MutableStateFlow(SignUpValidation())

    @OptIn(FlowPreview::class)
    private val combinedValidationState: StateFlow<SignUpValidation> = combine(
        formState,
        formValidationState,
    ) { form, validation ->
        val valid = SignUpValidator.validate(form)
        validation.copy(
            emailError = if (validation.showEmailError) valid.emailError else Option.None,
            usernameError = if (validation.showUsernameError) valid.usernameError else Option.None,
            passwordError = if (validation.showPasswordError) valid.passwordError else Option.None,
            confirmPasswordError = if (validation.showConfirmPasswordError) valid.confirmPasswordError else Option.None,
            termsError = if (validation.showTermsError) valid.termsError else Option.None,
            privacyPolicyError = if (validation.showPrivacyPolicyError) valid.privacyPolicyError else Option.None,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = formValidationState.value
    )

    private val _state: MutableStateFlow<SignUpState> = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = combine(
        _state,
        formState,
        combinedValidationState,
    ) { state, form, validation ->
        state.copy(
            signUpForm = form,
            formValidation = validation,
            submitEnabled = validation.valid() && !state.submitLoading,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value,
    )

    fun onEvent(event: SignUpEvents) {
        when (event) {
            is SignUpEvents.OnAliasNameChanged -> aliasNameChanged(event.value)
            is SignUpEvents.OnConfirmPasswordChanged -> confirmPasswordChanged(event.value)
            is SignUpEvents.OnEmailChanged -> emailChanged(event.value)
            is SignUpEvents.OnPasswordChanged -> passwordChanged(event.value)
            is SignUpEvents.OnUsernameChanged -> usernameChanged(event.value)
            SignUpEvents.OnSubmit -> submit()
            SignUpEvents.TogglePrivacyPolicy -> togglePrivacyPolicy()
            SignUpEvents.ToggleTerms -> toggleTerms()
            SignUpEvents.TogglePasswordVisibility -> togglePasswordVisibility()
            SignUpEvents.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            SignUpEvents.OnNavigateToSignIn -> navigator.pop()
            SignUpEvents.OnCheckEmailTaken -> checkEmailTaken()
            SignUpEvents.OnCheckUsernameTaken -> checkUsernameTaken()
        }
    }

    private fun aliasNameChanged(value: String) {
        formState.update { it.copy(alias = value) }
    }

    private fun emailChanged(value: String) {
        formState.update { state -> state.copy(email = value, isEmailTaken = false) }
        formValidationState.update { state -> state.copy(showEmailError = true) }
    }

    private fun usernameChanged(value: String) {
        formState.update { state ->
            state.copy(
                username = value,
                isUsernameTaken = false,
                alias = state.alias.ifEmpty { value }
            )
        }
        formValidationState.update { state -> state.copy(showUsernameError = true) }
    }

    private fun passwordChanged(value: String) {
        formState.update { state -> state.copy(password = value) }
        formValidationState.update { state -> state.copy(showPasswordError = true) }
    }

    private fun confirmPasswordChanged(value: String) {
        formState.update { state -> state.copy(confirmPassword = value) }
        formValidationState.update { state -> state.copy(showConfirmPasswordError = true) }
    }

    private fun toggleTerms() {
        formState.update { state -> state.copy(termsAndConditions = !state.termsAndConditions) }
        formValidationState.update { state -> state.copy(showTermsError = true) }
    }

    private fun togglePrivacyPolicy() {
        formState.update { state -> state.copy(privacyPolicy = !state.privacyPolicy) }
        formValidationState.update { state -> state.copy(showPrivacyPolicyError = true) }
    }

    private fun togglePasswordVisibility() {
        _state.update { state -> state.copy(passwordVisible = !state.passwordVisible) }
    }

    private fun toggleConfirmPasswordVisibility() {
        _state.update { state -> state.copy(confirmPasswordVisible = !state.confirmPasswordVisible) }
    }

    private fun submit() {
        _state.update { state ->
            state.copy(
                submitLoading = true,
                submitEnabled = false,
                submitSuccess = false
            )
        }

        formState.update { form ->
            form.copy(
                sessionData = SessionData(
                    device = config.deviceName,
                    agent = config.agent,
                )
            )
        }

        scope.launch {
            repository.signUp(formState.value).fold(
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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun checkEmailTaken() {
        scope.launch {
            uRepository.checkEmail(formState.value.email).fold(
                onFailure = { err -> toaster.show(err.toToast()) },
                onSuccess = { available ->
                    formState.update { it.copy(isEmailTaken = !available) }
                }
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun checkUsernameTaken() {
        scope.launch {
            uRepository.checkUsername(formState.value.username).fold(
                onFailure = { err -> toaster.show(err.toToast()) },
                onSuccess = { available ->
                    formState.update { it.copy(isUsernameTaken = !available) }
                }
            )
        }
    }
}
