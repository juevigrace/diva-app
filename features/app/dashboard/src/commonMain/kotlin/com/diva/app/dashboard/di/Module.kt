package com.diva.app.dashboard.di

import com.diva.app.dashboard.data.DashboardRepository
import com.diva.app.dashboard.data.DashboardRepositoryImpl
import com.diva.app.dashboard.presentation.viewmodel.DashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun dashboardModule(): Module {
    return module {
        singleOf(::DashboardRepositoryImpl) { bind<DashboardRepository>() }

        viewModelOf(::DashboardViewModel)
    }
}