package com.diva.app.di.database

import io.github.juevigrace.diva.database.driver.DriverProvider
import io.github.juevigrace.diva.database.driver.configuration.DriverConf
import io.github.juevigrace.diva.database.driver.configuration.NativeConf
import io.github.juevigrace.diva.database.driver.factory.NativeDriverProviderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun driverModule(): Module {
    return module {
        single<DriverProvider> {
            NativeDriverProviderFactory(
                NativeConf(DriverConf.SqliteDriverConf("diva.db"))
            ).create()
        }
    }
}
