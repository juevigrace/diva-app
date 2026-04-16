package com.diva.app.home.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.diva.app.creation.presentation.ui.screen.CreationScreen
import com.diva.app.dashboard.presentation.ui.screen.DashboardScreen
import com.diva.app.feed.presentation.ui.screen.FeedScreen
import com.diva.app.home.presentation.events.HomeEvents
import com.diva.app.home.presentation.viewmodel.HomeViewModel
import com.diva.ui.navigation.CreationDestination
import com.diva.ui.navigation.DashboardDestination
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.FeedDestination
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.components.layout.bars.bottom.BottomNavBar
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import io.github.juevigrace.diva.ui.components.navigation.Navigator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named
import io.github.juevigrace.diva.ui.navigation.Navigator as DivaNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabNavigator: DivaNavigator<Destination> = koinInject(named("home_tabs"))

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvents.OnRender)
    }

    Screen(
        topBar = {
            TopNavBar(
                title = {
                    Text(text = stringResource(state.currentTab.title))
                }
            )
        },
        bottomBar = {
            BottomNavBar {
                state.appTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(tab.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(tab.title)) },
                        selected = state.selectedTabIndex == index,
                        onClick = { viewModel.onEvent(HomeEvents.SelectTab(index)) },
                    )
                }
            }
        },
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
