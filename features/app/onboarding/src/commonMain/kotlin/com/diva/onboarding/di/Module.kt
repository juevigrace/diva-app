package com.diva.onboarding.di

import com.diva.onboarding.presentation.viewmodel.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun onboardingModule(): Module {
    return module {
        viewModelOf(::OnboardingViewModel)
    }
}
