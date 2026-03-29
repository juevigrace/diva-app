package com.diva.verification.presentation.ui.components.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.VerificationDestination
import com.diva.verification.presentation.ui.screen.VerificationScreen

fun EntryProviderScope<Destination>.verificationEntries() {
    entry<VerificationDestination> { destination ->
        VerificationScreen(destination.action)
    }
}
