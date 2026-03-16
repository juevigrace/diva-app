package com.diva.auth.signIn.di

import com.diva.auth.signIn.data.SignInRepository
import com.diva.auth.signIn.data.SignInRepositoryImpl
import com.diva.auth.signIn.presentation.viewmodel.SignInViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun signInModule(): Module {
    return module {
        singleOf(::SignInRepositoryImpl) { bind<SignInRepository>() }

        viewModelOf(::SignInViewModel)
    }
}
