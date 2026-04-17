package com.diva.app.profile.presentation.events

sealed interface ProfileEvents {
    data object OnRender : ProfileEvents
    data object SignOut : ProfileEvents
}
