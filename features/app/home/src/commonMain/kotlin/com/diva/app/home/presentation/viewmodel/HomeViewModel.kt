package com.diva.app.home.presentation.viewmodel

import com.diva.app.home.data.HomeRepository
import com.diva.app.home.presentation.events.HomeEvents
import com.diva.app.home.presentation.state.HomeState
import com.diva.models.actions.Actions
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.VerificationDestination
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.ui.navigation.bars.BottomBarState
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.core.util.logError
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.navigation.bars.NavBarState
import io.github.juevigrace.diva.ui.navigation.tab.Tab
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val navigator: Navigator<Destination>,
    private val tabNavigator: Navigator<Destination>,
    private val toaster: Toaster
) : DivaViewModel() {
    private val bottomBarState: MutableStateFlow<NavBarState> = MutableStateFlow(
        BottomBarState(tabs = HomeState.defaultAppTabs)
    )

    private val _state = MutableStateFlow(HomeState())
    val state = combine(
        _state,
        bottomBarState
    ) { state, bState ->
        state.copy(
            bottomBarState = bState
        )
    }.stateIn(
        scope,
        SharingStarted.WhileSubscribed(5000L),
        _state.value
    )

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
            is HomeEvents.OnTabUpdate -> updateTabFromRoute(event.route)
            is HomeEvents.SelectTab -> selectTab(event.tab)
            HomeEvents.ToggleDrawer -> toggleDrawer()
        }
    }

    private fun updateTabFromRoute(route: Destination) {
        val tab = bottomBarState.value.tabs.find { it.route as Destination == route }
        if (tab != null) {
            bottomBarState.update { state ->
                state.selectTab(tab)
            }
        }
    }

    private fun selectTab(tab: Tab) {
        when {
            tabNavigator.backStack.value.entries.contains(tab.route as Destination) -> {
                if (bottomBarState.value.selectedTab.getOrElse { null } != tab) {
                    tabNavigator.popUntil(tab.route as Destination)
                }
            }
            tabNavigator.backStack.value.entries.size >= 2 -> {
                tabNavigator.replaceTop(tab.route as Destination)
            }
            else -> {
                tabNavigator.navigate(tab.route as Destination)
            }
        }
        bottomBarState.update { state ->
            state.selectTab(tab)
        }
    }

    private fun toggleDrawer() {
        _state.update { state -> state.copy(drawerOpen = !state.drawerOpen) }
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
