package com.diva.app.presentation.viewmodel

import com.diva.app.presentation.state.AppState
import com.diva.models.user.preferences.UserPreferences
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.OnboardingDestination
import com.diva.ui.navigation.SignInDestination
import com.diva.user.data.UserRepository
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AppViewModel(
    private val uRepository: UserRepository,
    private val navigator: Navigator<Destination>
) : DivaViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    fun handleNavigation() {
        if (_state.value.preferences.onboardingCompleted) {
            navigator.replaceAll(SignInDestination)
        } else {
            navigator.replaceAll(OnboardingDestination)
        }
    }

    fun initialize() {
        handleGetPreferences { prefs ->
            _state.update { state ->
                state.copy(
                    preferences = prefs,
                    shouldNavigate = true
                )
            }
        }
    }

    fun handleGetPreferences(onSuccess: (UserPreferences) -> Unit) {
        scope.launch {
            uRepository.getLocalPreferences().collect { res ->
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
            uRepository.createPreferences(UserPreferences(id = Uuid.random())).collect { res ->
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
