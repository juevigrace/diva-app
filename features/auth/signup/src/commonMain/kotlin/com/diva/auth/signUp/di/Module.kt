package com.diva.auth.signUp.di

import com.diva.auth.signUp.data.SignUpRepository
import com.diva.auth.signUp.data.SignUpRepositoryImpl
import com.diva.auth.signUp.presentation.viewmodel.SignUpViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun signUpModule(): Module {
    return module {
        singleOf(::SignUpRepositoryImpl) { bind<SignUpRepository>() }

        viewModelOf(::SignUpViewModel)
    }
}
