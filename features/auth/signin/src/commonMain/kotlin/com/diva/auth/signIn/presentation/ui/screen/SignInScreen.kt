package com.diva.auth.signIn.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.presentation.components.AuthAccountNavRow
import com.diva.auth.presentation.components.AuthLayout
import com.diva.auth.signIn.presentation.events.SignInEvents
import com.diva.auth.signIn.presentation.state.SignInState
import com.diva.auth.signIn.presentation.ui.components.SignInForm
import com.diva.auth.signIn.presentation.viewmodel.SignInViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.dont_have_account
import com.diva.core.ui.resources.facebook
import com.diva.core.ui.resources.google
import com.diva.core.ui.resources.ic_facebook
import com.diva.core.ui.resources.ic_google
import com.diva.core.ui.resources.ic_x
import com.diva.core.ui.resources.or_continue_with
import com.diva.core.ui.resources.sign_up
import com.diva.core.ui.resources.twitter
import com.diva.models.config.AppConfig
import com.diva.ui.models.SocialProvider
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel()
) {
    val state: SignInState by viewModel.state.collectAsStateWithLifecycle()
    val appConfig: AppConfig = koinInject()

    AuthLayout {
        item {
            SignInForm(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        if (state.socialProviders.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(Res.string.or_continue_with),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.socialProviders.forEach { provider ->
                        OutlinedButton(
                            onClick = { viewModel.onEvent(SignInEvents.OnSocialLogin(provider)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        when (provider) {
                                            SocialProvider.Google -> Res.drawable.ic_google
                                            SocialProvider.Facebook -> Res.drawable.ic_facebook
                                            SocialProvider.Twitter -> Res.drawable.ic_x
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = when (provider) {
                                        SocialProvider.Google -> stringResource(Res.string.google)
                                        SocialProvider.Facebook -> stringResource(Res.string.facebook)
                                        SocialProvider.Twitter -> stringResource(Res.string.twitter)
                                    }
                                )
                            }
                        }
                    }

                    AuthAccountNavRow(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.dont_have_account),
                        buttonText = stringResource(Res.string.sign_up),
                        onClick = {
                            viewModel.onEvent(SignInEvents.OnNavigateToSignUp)
                        },
                    )
                }
            }
        }

        item {
            Text(
                text = "Ver. ${appConfig.versionName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
