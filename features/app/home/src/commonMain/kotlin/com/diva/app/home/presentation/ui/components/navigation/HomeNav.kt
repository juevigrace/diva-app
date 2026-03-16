package com.diva.app.home.presentation.ui.components.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diva.app.home.presentation.ui.screen.HomeScreen
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination

fun EntryProviderScope<Destination>.homeEntries() {
    entry<HomeDestination> {
        HomeScreen()
    }
}
