package com.diva.onboarding.presentation.events

sealed interface OnboardingEvents {
    data object OnNavigateToSignIn : OnboardingEvents
    data object OnNextPage : OnboardingEvents
    data object OnPreviousPage : OnboardingEvents
    data object OnSkip : OnboardingEvents
}
