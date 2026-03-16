package com.diva.onboarding.presentation.ui.components.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diva.onboarding.presentation.ui.screens.OnboardingScreen
import com.diva.onboarding.presentation.ui.screens.SplashScreen
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.OnboardingDestination
import com.diva.ui.navigation.SplashDestination

fun EntryProviderScope<Destination>.onboardingEntries() {
    entry<SplashDestination> {
        SplashScreen()
    }
    entry<OnboardingDestination> {
        OnboardingScreen()
    }
}
