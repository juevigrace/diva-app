package com.diva.auth.di

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.data.api.client.AuthApiImpl
import com.diva.auth.forgot.di.forgotModule
import com.diva.auth.session.di.sessionModule
import com.diva.auth.signIn.di.signInModule
import com.diva.auth.signUp.di.signUpModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun authModule(): Module {
    return module {
        singleOf(::AuthApiImpl) { bind<AuthApi>() }

        includes(
            forgotModule(),
            sessionModule(),
            signInModule(),
            signUpModule(),
        )
    }
}
