package com.diva.auth.signIn.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.signIn.presentation.state.SignInState
import com.diva.auth.signIn.presentation.viewmodel.SignInViewModel
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.ForgotDestination
import com.diva.ui.navigation.HomeDestination
import com.diva.ui.navigation.SignUpDestination
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel()
) {
    val state: SignInState by viewModel.state.collectAsStateWithLifecycle()
    val navigator: Navigator<Destination> = koinInject()

    Screen {
        Column {
            TextButton(
                onClick = {
                    navigator.navigate(SignUpDestination)
                }
            ) {
                Text("SignUp")
            }
            TextButton(
                onClick = {
                    navigator.navigate(ForgotDestination(ForgotAction.OnForgotPassword))
                }
            ) {
                Text("Forgot password")
            }
            TextButton(
                onClick = {
                    navigator.replaceAll(HomeDestination)
                }
            ) {
                Text("Home")
            }
        }
    }
}
