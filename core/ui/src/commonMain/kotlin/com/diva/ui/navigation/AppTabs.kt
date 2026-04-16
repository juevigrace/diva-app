package com.diva.ui.navigation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.creation
import com.diva.core.ui.resources.home
import com.diva.core.ui.resources.ic_books
import com.diva.core.ui.resources.ic_home
import com.diva.core.ui.resources.ic_library_plus
import com.diva.core.ui.resources.library
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class AppTabs(
    val title: StringResource,
    val icon: DrawableResource,
    val route: Destination,
) {
    data object Dashboard : AppTabs(Res.string.home, Res.drawable.ic_home, DashboardDestination)
    data object Library : AppTabs(Res.string.library, Res.drawable.ic_books, FeedDestination)
    data object Creation : AppTabs(Res.string.creation, Res.drawable.ic_library_plus, CreationDestination)
}
