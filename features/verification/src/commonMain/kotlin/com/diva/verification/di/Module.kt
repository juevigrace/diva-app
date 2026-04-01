package com.diva.verification.di

import com.diva.verification.data.VerificationRepository
import com.diva.verification.data.VerificationRepositoryImpl
import com.diva.verification.data.api.client.VerificationApi
import com.diva.verification.data.api.client.VerificationApiImpl
import com.diva.verification.presentation.viewmodel.VerificationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun verificationModule(): Module {
    return module {
        singleOf(::VerificationApiImpl) { bind<VerificationApi>() }

        singleOf(::VerificationRepositoryImpl) { bind<VerificationRepository>() }

        viewModelOf(::VerificationViewModel)
    }
}
