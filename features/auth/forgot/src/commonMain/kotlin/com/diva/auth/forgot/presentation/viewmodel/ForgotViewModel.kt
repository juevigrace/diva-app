package com.diva.auth.forgot.presentation.viewmodel

import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SignInDestination
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// TODO: create form class
class ForgotViewModel(
    private val action: ForgotAction,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster,
) : DivaViewModel() {
    private val _state = MutableStateFlow(ForgotState())
    val state: StateFlow<ForgotState> = _state.asStateFlow()

    fun onEvent(event: ForgotEvents) {
        when (event) {
            is ForgotEvents.OnEmailChanged -> emailChanged(event.email)
            is ForgotEvents.OnTokenChanged -> tokenChanged(event.token)
            is ForgotEvents.OnNewPasswordChanged -> newPasswordChanged(event.password)
            is ForgotEvents.OnConfirmPasswordChanged -> confirmPasswordChanged(event.password)
            ForgotEvents.OnSubmit -> TODO()
            ForgotEvents.OnBack -> TODO()
            ForgotEvents.OnNavigateToSignIn -> navigateToSignIn()
        }
    }

    private fun emailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun tokenChanged(token: String) {
        _state.update { it.copy(token = token, tokenError = null) }
    }

    private fun newPasswordChanged(password: String) {
        _state.update { it.copy(newPassword = password, passwordError = null) }
    }

    private fun confirmPasswordChanged(password: String) {
        _state.update { it.copy(confirmPassword = password, passwordError = null) }
    }

    private fun navigateToSignIn() {
        navigator.replaceAll(SignInDestination)
    }
}
