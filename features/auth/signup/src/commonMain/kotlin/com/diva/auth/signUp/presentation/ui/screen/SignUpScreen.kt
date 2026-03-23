package com.diva.auth.signUp.presentation.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.presentation.components.AuthLayout
import com.diva.auth.signUp.presentation.state.SignUpState
import com.diva.auth.signUp.presentation.viewmodel.SignUpViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state: SignUpState by viewModel.state.collectAsStateWithLifecycle()

    AuthLayout {
    }
}
