package com.diva.app.library.di

import com.diva.app.library.data.LibraryRepository
import com.diva.app.library.data.LibraryRepositoryImpl
import com.diva.app.library.presentation.viewmodel.LibraryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun libraryModule(): Module {
    return module {
        singleOf(::LibraryRepositoryImpl) { bind<LibraryRepository>() }

        viewModelOf(::LibraryViewModel)
    }
}