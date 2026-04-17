package com.diva.app.library.presentation.events

sealed class LibraryEvents {
    data object OnRender : LibraryEvents()
}