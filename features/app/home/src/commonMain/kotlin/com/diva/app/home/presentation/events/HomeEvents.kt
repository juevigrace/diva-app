package com.diva.app.home.presentation.events

import com.diva.ui.navigation.Destination
import io.github.juevigrace.diva.ui.navigation.tab.Tab

sealed interface HomeEvents {
    data object OnRender : HomeEvents
    data object ToggleDrawer : HomeEvents
    data class SelectTab(val tab: Tab) : HomeEvents
    data class OnTabUpdate(val route: Destination) : HomeEvents
}
