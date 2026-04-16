package com.diva.app.dashboard.presentation.viewmodel

import com.diva.app.dashboard.presentation.state.DashboardState
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : DivaViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()
}