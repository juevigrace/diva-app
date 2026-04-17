package com.diva.app.home.presentation.state

import com.diva.models.user.User
import com.diva.ui.navigation.bars.BottomBarState
import com.diva.ui.navigation.tab.AppTabs
import io.github.juevigrace.diva.ui.navigation.bars.NavBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class HomeState(
    val user: User = User(id = Uuid.NIL),

    val bottomBarState: NavBarState = BottomBarState(tabs = defaultAppTabs),

    val drawerOpen: Boolean = false,
) {
    companion object {
        val defaultAppTabs: List<AppTabs> = listOf(
            AppTabs.Feed,
            AppTabs.Library,
            AppTabs.Creation,
            AppTabs.Profile
        )
    }
}
