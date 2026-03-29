package com.diva.auth.presentation.components.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.diva.auth.forgot.presentation.ui.screen.ForgotScreen
import com.diva.auth.signIn.presentation.ui.screen.SignInScreen
import com.diva.auth.signUp.presentation.ui.screen.SignUpScreen
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.ForgotDestination
import com.diva.ui.navigation.SignInDestination
import com.diva.ui.navigation.SignUpDestination

fun EntryProviderScope<Destination>.authEntries() {
    entry<SignInDestination> {
        SignInScreen()
    }
    entry<SignUpDestination> {
        SignUpScreen()
    }
    entry<ForgotDestination> { destination ->
        ForgotScreen(destination.action)
    }
}
