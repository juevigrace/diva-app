package com.diva.verification.di

import com.diva.verification.presentation.viewmodel.VerificationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun verificationModule(): Module {
    return module {
        viewModelOf(::VerificationViewModel)
    }
}
