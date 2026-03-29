package com.diva.app.presentation.viewmodel

import com.diva.app.presentation.state.AppState
import com.diva.models.user.preferences.UserPreferences
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.OnboardingDestination
import com.diva.ui.navigation.SignInDestination
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.preferences.UserPreferencesRepository
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AppViewModel(
    private val actionRepository: UserActionsRepository,
    private val prefsRepository: UserPreferencesRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster
) : DivaViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    // TODO: handle navigation to home if session exists
    init {
        scope.launch {
            launch {
                handleGetPreferences { prefs ->
                    _state.update { state ->
                        state.copy(
                            preferences = prefs,
                            shouldNavigate = true
                        )
                    }
                }
            }
            launch{
                // TODO: session
            }
            launch {
                actionRepository.syncActions().collect { result ->
                    result.onFailure { err ->
                        val cause = err.cause
                        if (cause is ErrorCause.Validation.MissingValue && cause.field == "session") {
                            println(err)
                        } else {
                            println(err)
                            toaster.show(err.toToast())
                        }
                    }
                }
            }
        }
    }

    fun handleNavigation() {
        if (_state.value.preferences.onboardingCompleted) {
            navigator.replaceAll(SignInDestination)
        } else {
            navigator.replaceAll(OnboardingDestination)
        }
    }

    fun handleGetPreferences(onSuccess: (UserPreferences) -> Unit) {
        scope.launch {
            prefsRepository.getLocalPreferences().collect { res ->
                res.fold(
                    onFailure = { err ->
                        println(err)
                        _state.update { state ->
                            state.copy(
                                prefTries = state.prefTries + 1,
                                panic = state.prefTries >= 3,
                                shouldNavigate = false
                            )
                        }
                        handleNoLocalPreferences()
                    },
                    onSuccess = onSuccess
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun handleNoLocalPreferences() {
        if (_state.value.panic) return
        scope.launch {
            prefsRepository.createLocalPreferences(UserPreferences(id = Uuid.random())).collect { res ->
                res.fold(
                    onFailure = { err ->
                        println(err)
                        _state.update { state ->
                            state.copy(
                                panic = true,
                                shouldNavigate = false
                            )
                        }
                    },
                    onSuccess = {
                        handleGetPreferences { prefs ->
                            _state.update { state ->
                                state.copy(
                                    preferences = prefs,
                                    shouldNavigate = true
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
