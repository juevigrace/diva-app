package com.diva.user.di

import com.diva.database.user.UserStorage
import com.diva.database.user.actions.UserActionsStorage
import com.diva.database.user.permissions.UserPermissionsStorage
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.user.api.client.UserApi
import com.diva.user.api.client.UserApiImpl
import com.diva.user.api.client.actions.UserActionsApi
import com.diva.user.api.client.actions.UserActionsApiImpl
import com.diva.user.api.client.me.UserMeApi
import com.diva.user.api.client.me.UserMeApiImpl
import com.diva.user.api.client.preferences.UserPreferencesApi
import com.diva.user.api.client.preferences.UserPreferencesApiImpl
import com.diva.user.data.UserRepository
import com.diva.user.data.UserRepositoryImpl
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.actions.UserActionsRepositoryImpl
import com.diva.user.data.me.UserMeRepository
import com.diva.user.data.me.UserMeRepositoryImpl
import com.diva.user.data.preferences.UserPreferencesRepository
import com.diva.user.data.preferences.UserPreferencesRepositoryImpl
import com.diva.user.database.UserStorageImpl
import com.diva.user.database.actions.UserActionsStorageImpl
import com.diva.user.database.permissions.UserPermissionsStorageImpl
import com.diva.user.database.preferences.UserPreferencesStorageImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun userModule(): Module {
    return module {
        singleOf(::UserStorageImpl) { bind<UserStorage>() }
        singleOf(::UserPermissionsStorageImpl) { bind<UserPermissionsStorage>() }
        singleOf(::UserPreferencesStorageImpl) { bind<UserPreferencesStorage>() }
        singleOf(::UserActionsStorageImpl) { bind<UserActionsStorage>() }

        singleOf(::UserApiImpl) { bind<UserApi>() }
        singleOf(::UserActionsApiImpl) { bind<UserActionsApi>() }
        singleOf(::UserMeApiImpl) { bind<UserMeApi>() }
        singleOf(::UserPreferencesApiImpl) { bind<UserPreferencesApi>() }

        singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
        singleOf(::UserActionsRepositoryImpl) { bind<UserActionsRepository>() }
        singleOf(::UserMeRepositoryImpl) { bind<UserMeRepository>() }
        singleOf(::UserPreferencesRepositoryImpl) { bind<UserPreferencesRepository>() }
    }
}
