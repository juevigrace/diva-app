package com.diva.auth.forgot.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.auth.forgot.presentation.state.ForgotStep
import com.diva.auth.forgot.presentation.ui.components.PasswordResetSection
import com.diva.auth.forgot.presentation.ui.components.UserEmailSection
import com.diva.auth.forgot.presentation.viewmodel.ForgotViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.error_action_not_specified
import com.diva.core.ui.resources.forgot_password_title
import com.diva.core.ui.resources.go_back
import com.diva.core.ui.resources.ic_chevron_left
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.submit
import com.diva.core.ui.resources.success
import com.diva.ui.components.buttons.LoadingButton
import com.diva.ui.components.layout.ErrorLayout
import com.diva.ui.components.layout.VerticalScrollableLayout
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(
    action: ForgotAction,
    viewModel: ForgotViewModel = koinViewModel()
) {
    val state: ForgotState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ForgotEvents.OnRender(action))
    }

    LaunchedEffect(Unit, state.action) {
        if (state.action !is ForgotAction.Unspecified) {
            viewModel.onEvent(ForgotEvents.OnCheckAction)
        }
    }

    if (state.action is ForgotAction.Unspecified) {
        return ErrorLayout(
            message = stringResource(Res.string.error_action_not_specified)
        )
    }

    VerticalScrollableLayout(
        topBar = {
            TopNavBar(
                title = {
                    Text(text = stringResource(Res.string.forgot_password_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(ForgotEvents.OnBack) }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_left),
                            contentDescription = stringResource(Res.string.go_back)
                        )
                    }
                }
            )
        },
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(24.dp)
    ) {
        when (state.step) {
            ForgotStep.PasswordReset -> {
                PasswordResetSection(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
            ForgotStep.Email -> UserEmailSection(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
        item {
            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.onEvent(ForgotEvents.OnSubmit) },
                isLoading = state.submitLoading,
                enabled = state.submitEnabled,
            ) {
                if (state.submitSuccess) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_circle_check),
                        contentDescription = stringResource(Res.string.success),
                    )
                } else {
                    Text(text = stringResource(Res.string.submit))
                }
            }
        }
    }
}
