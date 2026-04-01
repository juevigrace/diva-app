package com.diva.app.di.network

import com.diva.models.config.AppConfig
import io.github.juevigrace.diva.network.client.factory.AndroidDivaClientFactory
import io.github.juevigrace.diva.network.client.factory.DivaClientFactory
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun networkFactoryModule(config: HttpClientConfig<*>.() -> Unit): Module {
    return module {
        single<DivaClientFactory> {
            val appConfig: AppConfig = get()
            AndroidDivaClientFactory(OkHttp) {
                config()
                installOrReplace(Logging) {
                    logger = Logger.ANDROID
                    level = if (appConfig.debug) {
                        LogLevel.ALL
                    } else {
                        LogLevel.NONE
                    }
                }
            }
        }
    }
}
