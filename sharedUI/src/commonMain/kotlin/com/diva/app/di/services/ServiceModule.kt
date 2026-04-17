package com.diva.app.di.services

import com.diva.models.config.AppConfig
import com.diva.services.SyncService
import com.diva.services.SyncServiceImpl
import io.github.juevigrace.diva.core.util.DivaLogger
import io.github.juevigrace.diva.core.util.LogLevel
import io.github.juevigrace.diva.core.util.createDivaLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun servicesModule(): Module {
    return module {
        singleOf(::SyncServiceImpl) { bind<SyncService>() }

        single<DivaLogger> {
            createDivaLogger().apply {
                val config: AppConfig = get()
                level = if (config.debug) {
                    LogLevel.DEBUG
                } else {
                    LogLevel.INFO
                }
            }
        }
    }
}
