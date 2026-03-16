package com.diva.app.home.di

import com.diva.app.home.data.HomeRepository
import com.diva.app.home.data.HomeRepositoryImpl
import com.diva.app.home.presentation.viewmodel.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun homeModule(): Module {
    return module {
        singleOf(::HomeRepositoryImpl) { bind<HomeRepository>() }

        viewModelOf(::HomeViewModel)
    }
}
