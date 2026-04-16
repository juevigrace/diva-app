package com.diva.app.home.presentation.viewmodel

import com.diva.app.home.data.HomeRepository
import com.diva.app.home.presentation.events.HomeEvents
import com.diva.app.home.presentation.state.HomeState
import com.diva.models.actions.Actions
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.AppTabs
import com.diva.ui.navigation.CreationDestination
import com.diva.ui.navigation.DashboardDestination
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.FeedDestination
import com.diva.ui.navigation.VerificationDestination
import com.diva.ui.navigation.arguments.VerificationAction
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.util.logError
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val navigator: Navigator<Destination>,
    private val tabNavigator: Navigator<Destination>,
    private val toaster: Toaster
) : DivaViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        scope.launch {
            tabNavigator.backStack.collect { backStack ->
                val current = backStack.current
                val newIndex = when {
                    current is Option.Some && current.value == DashboardDestination -> 0
                    current is Option.Some && current.value == FeedDestination -> 1
                    current is Option.Some && current.value == CreationDestination -> 2
                    else -> 0
                }
                if (newIndex != _state.value.selectedTabIndex) {
                    _state.update { it.copy(selectedTabIndex = newIndex) }
                }
            }
        }
    }

    private fun init() {
        scope.launch {
            launch {
                handleUser()
            }
            launch {
                handleActions()
            }
        }
    }

    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.OnRender -> init()
            is HomeEvents.SelectTab -> selectTab(event.index)
        }
    }

    private fun selectTab(index: Int) {
        val tab = _state.value.appTabs.getOrNull(index) ?: return
        val destination = tab.route

        if (index == 0) {
            tabNavigator.replaceAll(destination)
        } else {
            tabNavigator.navigate(destination)
        }
        _state.update { it.copy(selectedTabIndex = index) }
    }

    private suspend fun handleActions() {
        repository.getActions().collect { result ->
            result.fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "HomeViewModel", err.toString())
                    toaster.show(err.toToast())
                },
                onSuccess = { actions ->
                    actions[Actions.USER_VERIFICATION]?.let {
                        navigator.replaceAll(VerificationDestination(VerificationAction.UserVerification))
                    }
                }
            )
        }
    }

    private suspend fun handleUser() {
        repository.getMe().collect { result ->
            result.fold(
                onFailure = { err ->
                    logError(this::class.simpleName ?: "HomeViewModel", err.toString())
                    toaster.show(err.toToast())
                },
                onSuccess = { user ->
                    _state.update { state ->
                        state.copy(
                            user = user
                        )
                    }
                }
            )
        }
    }
}
