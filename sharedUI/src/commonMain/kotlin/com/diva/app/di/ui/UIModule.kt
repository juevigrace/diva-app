package com.diva.app.di.ui

import com.diva.ui.navigation.FeedDestination
import com.diva.ui.navigation.SplashDestination
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun uiModule(): Module {
    return module {
        single<Toaster> {
            Toaster.invoke()
        }
        single(named("app_router")) {
            Navigator.newInstance(SplashDestination)
        }
        single(named("home_tabs")) {
            Navigator.newInstance(FeedDestination)
        }
    }
}
