package com.diva.app.di.network

import com.diva.models.config.AppConfig
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.defaultConfig
import io.github.juevigrace.diva.network.client.factory.DivaClientFactory
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun networkFactoryModule(config: HttpClientConfig<*>.() -> Unit): Module

fun networkModule(config: AppConfig): Module {
    return module {
        includes(
            networkFactoryModule {
                defaultConfig()
                installOrReplace(Logging) {
                    logger = DivaClient.DEFAULT_LOGGER
                    level = if (config.debug) {
                        LogLevel.ALL
                    } else {
                        LogLevel.NONE
                    }
                }
                defaultRequest {
                    url(config.baseUrl)
                }
            }
        )
        single<DivaClient> {
            val factory: DivaClientFactory = get()
            factory.create()
        }
    }
}
