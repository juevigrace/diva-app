package com.diva.app.di.services

import com.diva.services.SyncService
import com.diva.services.SyncServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun servicesModule(): Module {
    return module {
        singleOf(::SyncServiceImpl) { bind<SyncService>() }
    }
}
