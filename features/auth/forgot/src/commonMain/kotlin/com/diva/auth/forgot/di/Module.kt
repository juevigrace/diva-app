package com.diva.auth.forgot.di

import com.diva.auth.forgot.data.ForgotRepository
import com.diva.auth.forgot.data.ForgotRepositoryImpl
import com.diva.auth.forgot.presentation.viewmodel.ForgotViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import com.diva.ui.navigation.Navigators
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun forgotModule(): Module {
    return module {
        singleOf(::ForgotRepositoryImpl) { bind<ForgotRepository>() }

        viewModel { ForgotViewModel(get(), get(named(Navigators.APP_ROUTER)), get()) }
    }
}
