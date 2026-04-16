package com.diva.app.home.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.diva.app.creation.presentation.ui.screen.CreationScreen
import com.diva.app.dashboard.presentation.ui.screen.DashboardScreen
import com.diva.app.feed.presentation.ui.screen.FeedScreen
import com.diva.app.home.presentation.events.HomeEvents
import com.diva.app.home.presentation.viewmodel.HomeViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.close_drawer
import com.diva.core.ui.resources.ic_menu
import com.diva.core.ui.resources.open_drawer
import com.diva.ui.navigation.CreationDestination
import com.diva.ui.navigation.DashboardDestination
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.FeedDestination
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.core.map
import io.github.juevigrace.diva.ui.components.layout.AdaptiveScreen
import io.github.juevigrace.diva.ui.components.layout.bars.bottom.BottomNavBar
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import io.github.juevigrace.diva.ui.components.navigation.Navigator
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.window.LocalWindowUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabNavigator: Navigator<Destination> = koinInject(named("home_tabs"))
    val windowUtils = LocalWindowUtils.current

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvents.OnRender)
    }
    AdaptiveScreen(
        topBar = {
            TopNavBar(
                title = {
                    state.bottomBarState.selectedTab.map { tab ->
                        Text(text = stringResource(tab.title))
                    }.getOrElse {}
                },
                navigationIcon = {
                    if (windowUtils.isLandscape) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(HomeEvents.ToggleDrawer)
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_menu),
                                contentDescription = stringResource(
                                    if (state.drawerOpen) {
                                        Res.string.close_drawer
                                    } else {
                                        Res.string.open_drawer
                                    }
                                )
                            )
                        }
                    }
                }
            )
        },
        drawerContent = {
            state.bottomBarState.tabs.forEach { tab ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(tab.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = stringResource(tab.title)) },
                    selected = state.bottomBarState.isSelected(tab),
                    onClick = { viewModel.onEvent(HomeEvents.SelectTab(tab)) },
                )
            }
        },
        bottomBar = {
            BottomNavBar {
                state.bottomBarState.tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(tab.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(tab.title)) },
                        selected = state.bottomBarState.isSelected(tab),
                        onClick = { viewModel.onEvent(HomeEvents.SelectTab(tab)) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Navigator(
                navigator = tabNavigator,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<DashboardDestination> {
                        DashboardScreen()
                    }
                    entry<FeedDestination> {
                        FeedScreen()
                    }
                    entry<CreationDestination> {
                        CreationScreen()
                    }
                }
            )
        }
    }
}
