package com.diva.app

import androidx.compose.ui.window.ComposeUIViewController
import com.diva.app.di.appModule
import com.diva.app.presentation.ui.screen.App
import com.diva.models.config.AppConfig
import org.koin.core.context.startKoin
import platform.UIKit.UIDevice
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    startKoin {
        modules(
            appModule(
                AppConfig(
                    version = "1.0",
                    deviceName = UIDevice.currentDevice.name,
                    agent = "Diva/1.0 (iOS; ${UIDevice.currentDevice.name})"
                )
            )
        )
    }

    App()
}
