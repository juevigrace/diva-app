package com.diva.app.profile.presentation.viewmodel

import com.diva.app.profile.presentation.state.ProfileState
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : DivaViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
}