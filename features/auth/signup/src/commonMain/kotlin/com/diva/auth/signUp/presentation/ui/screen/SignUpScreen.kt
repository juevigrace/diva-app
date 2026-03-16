package com.diva.auth.signUp.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.signUp.presentation.state.SignUpState
import com.diva.auth.signUp.presentation.viewmodel.SignUpViewModel
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state: SignUpState by viewModel.state.collectAsStateWithLifecycle()
    val navigator: Navigator<Destination> = koinInject()

    Screen { innerPadding ->
        Column {
            TextButton(
                onClick = { navigator.pop() },
            ) {
                Text("Sign In")
            }
            TextButton(
                onClick = { navigator.replaceAll(HomeDestination) }
            ) {
                Text("Home")
            }
        }
    }
}
