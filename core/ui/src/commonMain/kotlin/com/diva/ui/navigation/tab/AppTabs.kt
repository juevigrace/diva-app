package com.diva.ui.navigation.tab

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.creation
import com.diva.core.ui.resources.home
import com.diva.core.ui.resources.ic_books
import com.diva.core.ui.resources.ic_home
import com.diva.core.ui.resources.ic_library_plus
import com.diva.core.ui.resources.library
import com.diva.ui.navigation.CreationDestination
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.FeedDestination
import com.diva.ui.navigation.LibraryDestination
import io.github.juevigrace.diva.ui.navigation.tab.Tab
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class AppTabs(
    override val title: StringResource,
    override val icon: DrawableResource,
    override val route: Destination,
) : Tab {
    data object Library : AppTabs(Res.string.library, Res.drawable.ic_books, LibraryDestination)
    data object Creation : AppTabs(Res.string.creation, Res.drawable.ic_library_plus, CreationDestination)
    data object Feed : AppTabs(Res.string.home, Res.drawable.ic_home, FeedDestination)
}
