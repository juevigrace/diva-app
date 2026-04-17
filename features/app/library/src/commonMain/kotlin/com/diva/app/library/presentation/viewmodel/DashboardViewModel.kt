package com.diva.app.library.presentation.viewmodel

import com.diva.app.library.presentation.state.LibraryState
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryViewModel : DivaViewModel() {
    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()
}