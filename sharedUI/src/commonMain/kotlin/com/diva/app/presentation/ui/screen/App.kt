package com.diva.app.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.diva.app.home.presentation.ui.components.navigation.homeEntries
import com.diva.app.presentation.state.AppState
import com.diva.app.presentation.viewmodel.AppViewModel
import com.diva.auth.presentation.components.navigation.authEntries
import com.diva.onboarding.presentation.ui.components.navigation.onboardingEntries
import com.diva.ui.components.CommonBackHandler
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.FeedDestination
import com.diva.ui.navigation.HomeDestination
import com.diva.ui.theme.AppTypography
import com.diva.ui.theme.darkScheme
import com.diva.ui.theme.lightScheme
import com.diva.verification.presentation.ui.components.navigation.verificationEntries
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.components.navigation.Navigator
import io.github.juevigrace.diva.ui.components.toaster.LocalToaster
import io.github.juevigrace.diva.ui.components.toaster.Toaster
import io.github.juevigrace.diva.ui.components.wrappers.DivaApp
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.theme.DivaThemeConfig
import io.github.juevigrace.diva.ui.theme.ThemeScheme
import io.github.juevigrace.diva.ui.toast.Toaster
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Composable
fun App() {
    val viewModel: AppViewModel = koinViewModel()
    val state: AppState by viewModel.state.collectAsStateWithLifecycle()

    val toaster: Toaster = koinInject()
    val navigator: Navigator<Destination> = koinInject(named("app_router"))
    val backStack by navigator.backStack.collectAsStateWithLifecycle()
    val tabNavigator: Navigator<Destination> = koinInject(named("home_tabs"))
    val tabBackStack by tabNavigator.backStack.collectAsStateWithLifecycle()

    LaunchedEffect(state.shouldNavigate, state.sessionLoading) {
        delay(1000)
        if (state.shouldNavigate && !state.sessionLoading) {
            viewModel.handleNavigation()
        }
    }

    CommonBackHandler(
        enabled = backStack.current.getOrElse { null } == HomeDestination &&
            tabBackStack.current.getOrElse { null } != FeedDestination,
        onBack = {
            tabNavigator.pop()
        }
    )

    DivaApp(
        providers = arrayOf(LocalToaster provides toaster),
        themeConfig = DivaThemeConfig(
            themeScheme = ThemeScheme(
                light = lightScheme,
                dark = darkScheme
            ),
            typography = AppTypography
        )
    ) {
        Screen(
            snackBarHost = { Toaster() }
        ) { _ ->
            Navigator(
                modifier = Modifier.fillMaxSize(),
                navigator = navigator,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    onboardingEntries()
                    authEntries()
                    homeEntries()
                    verificationEntries()
                }
            )
        }
    }
}
