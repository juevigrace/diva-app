package com.diva.app.feed.presentation.viewmodel

import com.diva.app.feed.presentation.state.FeedState
import io.github.juevigrace.diva.ui.viewmodel.DivaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedViewModel : DivaViewModel() {
    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()
}