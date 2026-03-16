package com.diva.app.di.database

import io.github.juevigrace.diva.database.driver.DriverProvider
import io.github.juevigrace.diva.database.driver.configuration.DriverConf
import io.github.juevigrace.diva.database.driver.configuration.JvmConf
import io.github.juevigrace.diva.database.driver.factory.JvmDriverProviderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun driverModule(): Module {
    return module {
        single<DriverProvider> {
            JvmDriverProviderFactory(
                JvmConf(DriverConf.SqliteDriverConf("diva.db"))
            ).create()
        }
    }
}
