package com.diva.onboarding.presentation.viewmodel

import com.diva.onboarding.presentation.events.OnboardingEvents
import com.diva.onboarding.presentation.state.OnboardingState
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SignInDestination
import com.diva.user.data.preferences.UserPreferencesRepository
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class OnboardingViewModel(
    private val navigator: Navigator<Destination>,
    private val prefsRepository: UserPreferencesRepository,
    private val toaster: Toaster,
) : DivaViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvents) {
        when (event) {
            OnboardingEvents.OnNavigateToSignIn -> handleOnboardingCompleted()
            OnboardingEvents.OnNextPage -> nextPage()
            OnboardingEvents.OnPreviousPage -> previousPage()
            OnboardingEvents.OnSkip -> skipToLastPage()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun handleOnboardingCompleted() {
        scope.launch {
            prefsRepository.getLocalPreferences().fold(
                onFailure = { err -> toaster.show(err.toToast()) },
                onSuccess = { prefs ->
                    prefsRepository.updatePreferences(prefs.copy(onboardingCompleted = true)).onFailure { err ->
                        toaster.show(err.toToast())
                    }
                }
            )
        }
        navigator.replaceAll(SignInDestination)
    }

    private fun nextPage() {
        _state.update { state ->
            if (state.page == state.pages.lastIndex) return@update state
            state.copy(page = state.page + 1)
        }
    }

    private fun previousPage() {
        _state.update { state ->
            if (state.page == 0) return@update state
            state.copy(page = state.page - 1)
        }
    }

    private fun skipToLastPage() {
        _state.update { state ->
            state.copy(page = state.pages.lastIndex)
        }
    }
}
