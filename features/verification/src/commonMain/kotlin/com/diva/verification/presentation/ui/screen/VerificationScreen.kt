package com.diva.verification.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.error_action_not_specified
import com.diva.core.ui.resources.go_back
import com.diva.core.ui.resources.ic_chevron_left
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.success
import com.diva.core.ui.resources.verification
import com.diva.core.ui.resources.verify
import com.diva.ui.components.layout.VerticalScrollableLayout
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.presentation.events.VerificationEvents
import com.diva.verification.presentation.ui.components.VerifyTokenSection
import com.diva.verification.presentation.viewmodel.VerificationViewModel
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    action: VerificationAction,
    viewModel: VerificationViewModel = koinViewModel(parameters = { parametersOf(action) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(VerificationEvents.OnSetAction(action))
    }

    LaunchedEffect(state.action) {
        if (state.action !is VerificationAction.Unspecified) {
            viewModel.onEvent(VerificationEvents.OnRequest)
        }
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
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.action) {
                    VerificationAction.Unspecified -> {
                        Text(
                            text = stringResource(Res.string.error_action_not_specified),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    else -> {
                        VerifyTokenSection(
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                    }
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.submitEnabled,
                    onClick = {
                        viewModel.onEvent(VerificationEvents.OnSubmit)
                    },
                ) {
                    AnimatedVisibility(
                        visible = state.submitLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    AnimatedVisibility(
                        visible = !state.submitLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (state.submitSuccess) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(Res.drawable.ic_circle_check),
                                contentDescription = stringResource(Res.string.success),
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.verify),
                            )
                        }
                    }
                }
            }
        }
    }
}
