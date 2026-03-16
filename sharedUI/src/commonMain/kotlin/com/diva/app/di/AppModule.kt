package com.diva.app.di

import com.diva.app.di.database.databaseModule
import com.diva.app.di.network.networkModule
import com.diva.app.di.ui.uiModule
import com.diva.app.home.di.homeModule
import com.diva.app.presentation.viewmodel.AppViewModel
import com.diva.auth.di.authModule
import com.diva.models.config.AppConfig
import com.diva.onboarding.di.onboardingModule
import com.diva.user.di.userModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule(config: AppConfig): Module {
    return module {
        single { config }

        includes(
            databaseModule(),
            networkModule(config),
            uiModule(),
        )
        includes(
            homeModule(),
            onboardingModule(),
        )
        includes(
            authModule(),
            userModule(),
        )

        viewModelOf(::AppViewModel)
    }
}
