package com.diva.app.home.di

import com.diva.app.creation.di.creationModule
import com.diva.app.feed.di.feedModule
import com.diva.app.home.data.HomeRepository
import com.diva.app.home.data.HomeRepositoryImpl
import com.diva.app.home.presentation.viewmodel.HomeViewModel
import com.diva.app.library.di.libraryModule
import com.diva.app.profile.di.profileModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun homeModule(): Module {
    return module {
        includes(
            libraryModule(),
            feedModule(),
            creationModule(),
            profileModule(),
        )

        singleOf(::HomeRepositoryImpl) { bind<HomeRepository>() }

        viewModel { HomeViewModel(get(), get(named("app_router")), get(named("home_tabs")), get()) }
    }
}
