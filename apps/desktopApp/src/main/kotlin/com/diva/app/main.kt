package com.diva.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.diva.app.di.appModule
import com.diva.app.presentation.ui.screen.App
import com.diva.models.config.AppConfig
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(
            appModule(
                AppConfig(
                    version = "1.0",
                    deviceName = "Desktop",
                    agent = "Diva/1.0 (Desktop)"
                )
            )
        )
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Diva",
    ) {
        App()
    }
}
