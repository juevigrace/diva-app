package com.diva.app.creation.presentation.viewmodel

import com.diva.app.creation.presentation.state.CreationState
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreationViewModel : DivaViewModel() {
    private val _state = MutableStateFlow(CreationState())
    val state = _state.asStateFlow()
}