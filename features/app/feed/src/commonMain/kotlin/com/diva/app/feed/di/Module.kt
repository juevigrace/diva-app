package com.diva.app.feed.di

import com.diva.app.feed.data.FeedRepository
import com.diva.app.feed.data.FeedRepositoryImpl
import com.diva.app.feed.presentation.viewmodel.FeedViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun feedModule(): Module {
    return module {
        singleOf(::FeedRepositoryImpl) { bind<FeedRepository>() }

        viewModelOf(::FeedViewModel)
    }
}