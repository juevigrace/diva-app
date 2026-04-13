package com.diva.app.home.presentation.events

sealed interface HomeEvents {
    data object OnRender : HomeEvents
}
