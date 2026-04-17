package com.diva.app.profile.presentation.viewmodel

import com.diva.app.profile.presentation.events.ProfileEvents
import com.diva.app.profile.presentation.state.ProfileState
import com.diva.auth.session.data.SessionRepository
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.success
import com.diva.ui.messages.toToast
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SignInDestination
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.ToastMessage
import io.github.juevigrace.diva.ui.toast.Toaster
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ProfileViewModel(
    private val sRepo: SessionRepository,
    private val toaster: Toaster,
    private val navigator: Navigator<Destination>
) : DivaViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    fun onEvent(event: ProfileEvents) {
        when (event) {
            ProfileEvents.OnRender -> {}
            ProfileEvents.SignOut -> signOut()
        }
    }

    private fun signOut() {
        scope.launch {
            sRepo.logout().fold(
                onFailure = { err ->
                    toaster.show(err.toToast())
                },
                onSuccess = {
                    toaster.show(ToastMessage(getString(Res.string.success)))
                    navigator.replaceAll(SignInDestination)
                }
            )
        }
    }
}
