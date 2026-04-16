package com.diva.auth.signUp.di

import com.diva.auth.signUp.data.SignUpRepository
import com.diva.auth.signUp.data.SignUpRepositoryImpl
import com.diva.auth.signUp.presentation.viewmodel.SignUpViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun signUpModule(): Module {
    return module {
        singleOf(::SignUpRepositoryImpl) { bind<SignUpRepository>() }

        viewModel { SignUpViewModel(get(), get(), get(named("app_router")), get(), get()) }
    }
}
