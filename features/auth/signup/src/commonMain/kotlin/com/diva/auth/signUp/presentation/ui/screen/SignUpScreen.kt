package com.diva.auth.signUp.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.presentation.components.AuthAccountNavRow
import com.diva.auth.presentation.components.AuthLayout
import com.diva.auth.signUp.presentation.events.SignUpEvents
import com.diva.auth.signUp.presentation.state.SignUpState
import com.diva.auth.signUp.presentation.ui.components.SignUpForm
import com.diva.auth.signUp.presentation.viewmodel.SignUpViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.dont_have_account
import com.diva.core.ui.resources.sign_in
import io.github.juevigrace.diva.core.Option
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state: SignUpState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.signUpForm.email) {
        if (state.formValidation.showEmailError && state.formValidation.emailError is Option.None) {
            viewModel.onEvent(SignUpEvents.OnCheckEmailTaken)
        }
    }

    LaunchedEffect(state.signUpForm.username) {
        if (state.formValidation.showUsernameError && state.formValidation.usernameError is Option.None) {
            viewModel.onEvent(SignUpEvents.OnCheckUsernameTaken)
        }
    }

    AuthLayout {
        item {
            SignUpForm(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        item {
            AuthAccountNavRow(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.dont_have_account),
                buttonText = stringResource(Res.string.sign_in),
                onClick = {
                    viewModel.onEvent(SignUpEvents.OnNavigateToSignIn)
                },
            )
        }
    }
}
