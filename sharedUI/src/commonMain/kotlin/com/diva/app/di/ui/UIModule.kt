package com.diva.app.di.ui

import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SplashDestination
import io.github.juevigrace.diva.ui.navigation.Navigator
import io.github.juevigrace.diva.ui.toast.Toaster
import org.koin.core.module.Module
import org.koin.dsl.module

fun uiModule(): Module {
    return module {
        single<Toaster> {
            Toaster.invoke()
        }
        single<Navigator<Destination>> {
            Navigator.newInstance(SplashDestination)
        }
    }
}
