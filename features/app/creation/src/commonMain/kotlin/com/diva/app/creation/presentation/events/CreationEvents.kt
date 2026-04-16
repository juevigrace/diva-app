package com.diva.app.creation.presentation.events

sealed class CreationEvents {
    data object OnRender : CreationEvents()
}