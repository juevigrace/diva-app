package com.diva.ui.navigation.bars

import io.github.juevigrace.diva.ui.navigation.bars.NavBarState
import io.github.juevigrace.diva.ui.navigation.tab.Tab

data class BottomBarState(
    override val tabs: List<Tab> = emptyList(),
    override val selectedTabIndex: Int = 0,
    override val showBar: Boolean = true
) : NavBarState {
    override fun updateIndex(index: Int): NavBarState {
        return copy(selectedTabIndex = index)
    }

    override fun toggleBar(): NavBarState {
        return copy(showBar = !showBar)
    }
}
