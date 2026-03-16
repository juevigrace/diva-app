package com.diva.verification.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.ui.navigation.Destination
import com.diva.ui.navigation.HomeDestination
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.presentation.viewmodel.VerificationViewModel
import io.github.juevigrace.diva.ui.components.layout.Screen
import io.github.juevigrace.diva.ui.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    action: VerificationAction,
    viewModel: VerificationViewModel = koinViewModel(parameters = { parametersOf(action) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigator: Navigator<Destination> = koinInject()

    Screen { innerPadding ->
        Column {
            TextButton(
                onClick = { navigator.replaceAll(HomeDestination) },
            ) {
                Text("Home")
            }
        }
    }
}
