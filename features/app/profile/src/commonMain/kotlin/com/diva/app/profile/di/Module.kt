package com.diva.app.profile.di

import com.diva.app.profile.data.ProfileRepository
import com.diva.app.profile.data.ProfileRepositoryImpl
import com.diva.app.profile.presentation.viewmodel.ProfileViewModel
import com.diva.ui.navigation.Navigators
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun profileModule(): Module {
    return module {
        singleOf(::ProfileRepositoryImpl) { bind<ProfileRepository>() }

        viewModel {
            ProfileViewModel(
                sRepo = get(),
                toaster = get(),
                navigator = get(named(Navigators.APP_ROUTER))
            )
        }
    }
}
