package com.diva.app.di.network

import io.github.juevigrace.diva.network.client.factory.DivaClientFactory
import io.github.juevigrace.diva.network.client.factory.JvmDivaClientFactory
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun networkFactoryModule(config: HttpClientConfig<*>.() -> Unit): Module {
    return module {
        single<DivaClientFactory> {
            JvmDivaClientFactory(OkHttp, config)
        }
    }
}
