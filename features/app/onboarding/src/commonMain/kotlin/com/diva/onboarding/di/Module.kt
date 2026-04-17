package com.diva.onboarding.di

import com.diva.onboarding.presentation.viewmodel.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import com.diva.ui.navigation.Navigators
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun onboardingModule(): Module {
    return module {
        viewModel { OnboardingViewModel(get(named(Navigators.APP_ROUTER)), get(), get()) }
    }
}
