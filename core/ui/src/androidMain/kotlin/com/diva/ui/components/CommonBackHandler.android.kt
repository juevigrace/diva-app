package com.diva.ui.components

import androidx.activity.compose.BackHandler

@androidx.compose.runtime.Composable
actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}
