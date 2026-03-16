package com.diva.app

import android.app.Application
import android.os.Build
import com.diva.app.di.appModule
import com.diva.models.config.AppConfig
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.config.Environment
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DivaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val environment = Environment.entries.find {
            BuildConfig.FLAVOR.uppercase() == it.name
        } ?: Environment.PRODUCTION

        val config = AppConfig(
            debug = BuildConfig.DEBUG,
            environment = environment,
            domain = BuildConfig.DOMAIN,
            version = BuildConfig.VERSION_NAME,
            deviceName = Build.MODEL,
            port = if (environment != Environment.DEVELOPMENT) {
                Option.None
            } else {
                Option.Some(BuildConfig.PORT.toInt())
            },
            agent = "Diva/${BuildConfig.VERSION_NAME} (Linux; Android ${Build.VERSION.SDK_INT}; ${Build.MODEL})"
        )

        startKoin {
            modules(appModule(config))
            androidContext(this@DivaApp)
            androidLogger()
        }
    }
}
