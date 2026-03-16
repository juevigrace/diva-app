package com.diva.app.di.network

import io.github.juevigrace.diva.network.client.factory.AppleDivaClientFactory
import io.github.juevigrace.diva.network.client.factory.DivaClientFactory
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun networkFactoryModule(config: HttpClientConfig<*>.() -> Unit): Module {
    return module {
        single<DivaClientFactory> {
            AppleDivaClientFactory(Darwin, config)
        }
    }
}
