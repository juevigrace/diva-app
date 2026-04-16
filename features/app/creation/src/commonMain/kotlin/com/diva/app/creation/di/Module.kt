package com.diva.app.creation.di

import com.diva.app.creation.data.CreationRepository
import com.diva.app.creation.data.CreationRepositoryImpl
import com.diva.app.creation.presentation.viewmodel.CreationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun creationModule(): Module {
    return module {
        singleOf(::CreationRepositoryImpl) { bind<CreationRepository>() }

        viewModelOf(::CreationViewModel)
    }
}