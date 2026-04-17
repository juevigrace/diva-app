package com.diva.app.di

import com.diva.app.data.AppRepository
import com.diva.app.data.AppRepositoryImpl
import com.diva.app.di.database.databaseModule
import com.diva.app.di.network.networkModule
import com.diva.app.di.services.servicesModule
import com.diva.app.di.ui.uiModule
import com.diva.app.home.di.homeModule
import com.diva.app.presentation.viewmodel.AppViewModel
import com.diva.auth.di.authModule
import com.diva.models.config.AppConfig
import com.diva.onboarding.di.onboardingModule
import com.diva.user.di.userModule
import com.diva.verification.di.verificationModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import com.diva.ui.navigation.Navigators
import org.koin.core.qualifier.named
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
            servicesModule(),
        )
        includes(
            authModule(),
            userModule(),
            verificationModule(),
        )

        singleOf(::AppRepositoryImpl) { bind<AppRepository>() }

        viewModel { AppViewModel(get(), get(named(Navigators.APP_ROUTER)), get()) }
    }
}
