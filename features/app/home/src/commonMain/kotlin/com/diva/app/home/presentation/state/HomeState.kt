package com.diva.app.home.presentation.state

import com.diva.models.user.User
import com.diva.ui.navigation.AppTabs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class HomeState(
    val user: User = User(id = Uuid.NIL),
    val appTabs: List<AppTabs> = defaultAppTabs,
    val selectedTabIndex: Int = 0,
) {
    val currentTab: AppTabs = appTabs[selectedTabIndex]
    companion object {
        val defaultAppTabs: List<AppTabs> = listOf(
            AppTabs.Dashboard,
            AppTabs.Creation,
            AppTabs.Library,
        )
    }
}
