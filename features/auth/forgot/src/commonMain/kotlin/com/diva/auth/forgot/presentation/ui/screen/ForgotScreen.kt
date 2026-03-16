package com.diva.auth.forgot.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.auth.forgot.presentation.viewmodel.ForgotViewModel
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.SignInDestination
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(
    action: ForgotAction,
    viewModel: ForgotViewModel = koinViewModel(parameters = { parametersOf(action) })
) {
    val state: ForgotState by viewModel.state.collectAsStateWithLifecycle()
    val navigator: Navigator<Destination> = koinInject()

    Screen {
        Column {
            TextButton(onClick = {
                navigator.pop()
            }) {
                Text("Back")
            }
            TextButton(onClick = {
                navigator.replaceAll(SignInDestination)
            }) {
                Text("Sign in")
            }
        }
    }
}
