package com.diva.verification.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.error_action_not_specified
import com.diva.core.ui.resources.go_back
import com.diva.core.ui.resources.ic_chevron_left
import com.diva.core.ui.resources.ic_chevron_right
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.resend
import com.diva.core.ui.resources.resend_verification_text
import com.diva.core.ui.resources.success
import com.diva.core.ui.resources.verification
import com.diva.core.ui.resources.verification_code
import com.diva.core.ui.resources.verification_code_description
import com.diva.core.ui.resources.verification_code_placeholder
import com.diva.core.ui.resources.verify
import com.diva.ui.components.buttons.LoadingButton
import com.diva.ui.components.layout.ErrorLayout
import com.diva.ui.components.layout.VerticalScrollableLayout
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.presentation.events.VerificationEvents
import com.diva.verification.presentation.viewmodel.VerificationViewModel
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    action: VerificationAction,
    viewModel: VerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(VerificationEvents.OnSetAction(action))
    }

    LaunchedEffect(Unit, state.action) {
        if (state.action !is VerificationAction.Unspecified) {
            viewModel.onEvent(VerificationEvents.OnRequest)
        }
    }

    if (state.action is VerificationAction.Unspecified) {
        return ErrorLayout(
            title = stringResource(Res.string.verification),
            message = stringResource(Res.string.error_action_not_specified)
        )
    }

    VerticalScrollableLayout(
        topBar = {
            TopNavBar(
                title = {
                    Text(text = stringResource(Res.string.verification))
                },
                navigationIcon = {
                    if (state.canGoBack) {
                        IconButton(
                            onClick = { viewModel.onEvent(VerificationEvents.OnBack) }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_chevron_left),
                                contentDescription = stringResource(Res.string.go_back)
                            )
                        }
                    }
                }
            )
        },
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.verification_code_description),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item {
            val focusManager = LocalFocusManager.current

            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.verificationForm.token,
                    onValueChange = { newValue ->
                        viewModel.onEvent(VerificationEvents.OnTokenChanged(newValue))
                    },
                    label = { Text(text = stringResource(Res.string.verification_code)) },
                    placeholder = { Text(text = stringResource(Res.string.verification_code_placeholder)) },
                    supportingText = when (val text = state.formValidation.tokenError) {
                        Option.None -> null
                        is Option.Some -> { { Text(text = text.value) } }
                    },
                    isError = state.formValidation.tokenError.isPresent(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (state.submitEnabled) {
                                viewModel.onEvent(VerificationEvents.OnSubmit)
                            }
                        }
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.resend_verification_text),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.clickable(
                            enabled = state.resendEnabled,
                            role = Role.Button,
                            onClick = { viewModel.onEvent(VerificationEvents.OnRequest) }
                        ).clip(CircleShape),
                        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.resend),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                        )
                        Text(
                            text = "${state.resendCountdown}s",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        item {
            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.onEvent(VerificationEvents.OnSubmit)
                },
                enabled = state.submitEnabled,
                isLoading = state.submitLoading,
            ) {
                if (state.submitSuccess) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_circle_check),
                        contentDescription = stringResource(Res.string.success),
                    )
                } else {
                    Text(text = stringResource(Res.string.verify))
                }
            }
        }
    }
}
