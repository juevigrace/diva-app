package com.diva.app.home.presentation.viewmodel

import com.diva.app.home.data.HomeRepository
import com.diva.app.home.presentation.events.HomeEvents
import com.diva.app.home.presentation.state.HomeState
import com.diva.models.actions.Actions
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.VerificationDestination
import com.diva.ui.navigation.arguments.VerificationAction
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val navigator: Navigator<Destination>,
    private val toaster: Toaster
) : DivaViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.OnActions -> handleActions()
        }
    }

    private fun handleActions() {
        scope.launch {
            repository.getActions().collect { result ->
                result.fold(
                    onFailure = { err ->
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
    }
}
