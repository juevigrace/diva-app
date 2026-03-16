package com.diva.app.di.database

import io.github.juevigrace.diva.database.driver.DriverProvider
import io.github.juevigrace.diva.database.driver.configuration.AndroidConf
import io.github.juevigrace.diva.database.driver.configuration.DriverConf
import io.github.juevigrace.diva.database.driver.factory.AndroidDriverProviderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun driverModule(): Module {
    return module {
        single<DriverProvider> {
            AndroidDriverProviderFactory(
                conf = AndroidConf(
                    context = get(),
                    driverConf = DriverConf.SqliteDriverConf("diva.db")
                )
            ).create()
        }
    }
}
