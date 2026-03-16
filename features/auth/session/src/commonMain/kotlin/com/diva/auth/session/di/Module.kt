package com.diva.auth.session.di

import com.diva.auth.session.data.SessionRepository
import com.diva.auth.session.data.SessionRepositoryImpl
import com.diva.auth.session.database.SessionStorageImpl
import com.diva.database.session.SessionStorage
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun sessionModule(): Module {
    return module {
        singleOf(::SessionStorageImpl) { bind<SessionStorage>() }

        singleOf(::SessionRepositoryImpl) { bind<SessionRepository>() }
    }
}
