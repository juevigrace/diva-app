package com.diva.user.di

import com.diva.database.user.UserStorage
import com.diva.user.api.client.UserNetworkClient
import com.diva.user.api.client.UserNetworkClientImpl
import com.diva.user.data.UserRepository
import com.diva.user.data.UserRepositoryImpl
import com.diva.user.database.UserStorageImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun userModule(): Module {
    return module {
        singleOf(::UserStorageImpl) { bind<UserStorage>() }
        singleOf(::UserNetworkClientImpl) { bind<UserNetworkClient>() }
        singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    }
}
