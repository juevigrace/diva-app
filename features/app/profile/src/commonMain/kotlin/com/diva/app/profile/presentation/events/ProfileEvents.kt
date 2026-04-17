package com.diva.app.profile.presentation.events

sealed class ProfileEvents {
    data object OnRender : ProfileEvents()
}