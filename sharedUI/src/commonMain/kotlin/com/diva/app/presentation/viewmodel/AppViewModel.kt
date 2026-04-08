package com.diva.app.presentation.viewmodel

import com.diva.app.data.AppRepository
import com.diva.app.presentation.state.AppState
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.session_expired
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination
import com.diva.ui.navigation.OnboardingDestination
import com.diva.ui.navigation.SignInDestination
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.ToastMessage
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AppViewModel(
    private val repository: AppRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster
) : DivaViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        scope.launch {
            launch {
                handleGetPreferences()
            }
            launch {
                handleSync()
            }
        }
    }

    fun handleNavigation() {
        if (_state.value.sessionSuccess) {
            navigator.replaceAll(HomeDestination)
            return
        }

        if (_state.value.preferences.onboardingCompleted) {
            navigator.replaceAll(SignInDestination)
        } else {
            navigator.replaceAll(OnboardingDestination)
        }
    }

    private suspend fun handleSync() {
        _state.update { state ->
            state.copy(
                sessionLoading = true,
                sessionSuccess = false
            )
        }
        repository.sync().collect { result ->
            result.fold(
                onFailure = { err ->
                    println(err)
                    if (err is ConstraintException && err.field == "session") {
                        _state.update { state ->
                            state.copy(
                                sessionLoading = false,
                                sessionSuccess = false
                            )
                        }

                        if (err.constraint == "expired") {
                            toaster.show(
                                ToastMessage(
                                    message = getString(Res.string.session_expired),
                                    isError = true,
                                )
                            )
                        }
                    } else {
                        toaster.show(err.toToast())
                    }
                },
                onSuccess = {
                    _state.update { state ->
                        state.copy(
                            sessionLoading = false,
                            sessionSuccess = true
                        )
                    }
                }
            )
        }
    }

    private suspend fun handleGetPreferences() {
        repository.getPreferences().collect { res ->
            res.fold(
                onFailure = { err ->
                    if (err is ConstraintException) {
                        println("Creating local preferences: $err")
                        handleNoLocalPreferences()
                    } else {
                        toaster.show(err.toToast())
                    }
                },
                onSuccess = { prefs ->
                    _state.update { state ->
                        state.copy(
                            preferences = prefs,
                            shouldNavigate = true
                        )
                    }
                }
            )
        }
    }

    private suspend fun handleNoLocalPreferences() {
        if (_state.value.panic) return
        repository.createLocalPreferences().collect { res ->
            res.fold(
                onFailure = { err ->
                    toaster.show(err.toToast())
                    _state.update { state ->
                        state.copy(
                            panic = true,
                            shouldNavigate = false
                        )
                    }
                },
                onSuccess = {
                    handleGetPreferences()
                }
            )
        }
    }
}
