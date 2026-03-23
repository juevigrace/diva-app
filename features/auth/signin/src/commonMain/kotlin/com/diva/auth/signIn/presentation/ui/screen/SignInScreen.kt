package com.diva.auth.signIn.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.auth.presentation.components.AuthLayout
import com.diva.auth.signIn.presentation.events.SignInEvents
import com.diva.auth.signIn.presentation.state.SignInState
import com.diva.auth.signIn.presentation.viewmodel.SignInViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.dont_have_account
import com.diva.core.ui.resources.email_or_username
import com.diva.core.ui.resources.email_placeholder
import com.diva.core.ui.resources.facebook
import com.diva.core.ui.resources.forgot_password
import com.diva.core.ui.resources.google
import com.diva.core.ui.resources.ic_chevron_right
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.ic_facebook
import com.diva.core.ui.resources.ic_google
import com.diva.core.ui.resources.ic_lock
import com.diva.core.ui.resources.ic_mail
import com.diva.core.ui.resources.ic_x
import com.diva.core.ui.resources.or_continue_with
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_placeholder
import com.diva.core.ui.resources.sign_in
import com.diva.core.ui.resources.sign_up
import com.diva.core.ui.resources.success
import com.diva.core.ui.resources.twitter
import com.diva.ui.components.input.SecureTextField
import com.diva.ui.models.SocialProvider
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel()
) {
    val state: SignInState by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val appConfig: AppConfig = koinInject()

    AuthLayout {
        Column(
            modifier = Modifier.widthIn(min = 250.dp, max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.signInForm.username,
                    onValueChange = { newValue ->
                        viewModel.onEvent(SignInEvents.OnUsernameChanged(newValue))
                    },
                    label = {
                        Text(text = stringResource(Res.string.email_or_username))
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.email_placeholder))
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_mail),
                            contentDescription = null
                        )
                    },
                    supportingText = when (val text = state.formValidation.usernameError) {
                        Option.None -> null
                        is Option.Some -> {
                            {
                                Text(
                                    text = stringResource(text.value),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    },
                    isError = state.formValidation.usernameError.isPresent(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SecureTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.signInForm.password,
                        onValueChange = { newValue ->
                            viewModel.onEvent(SignInEvents.OnPasswordChanged(newValue))
                        },
                        label = stringResource(Res.string.password),
                        placeholder = stringResource(Res.string.password_placeholder),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Res.drawable.ic_lock),
                                contentDescription = null
                            )
                        },
                        supportingText = when (val text = state.formValidation.passwordError) {
                            Option.None -> null
                            is Option.Some -> stringResource(text.value)
                        },
                        isError = state.formValidation.passwordError.isPresent(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (state.submitEnabled) {
                                    viewModel.onEvent(SignInEvents.OnSubmit)
                                }
                            }
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.clickable(
                                onClick = {
                                    viewModel.onEvent(SignInEvents.OnForgot(ForgotAction.Password))
                                },
                                role = Role.Button
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.forgot_password),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.submitEnabled,
                    colors = ButtonDefaults.buttonColors(),
                    onClick = {
                        viewModel.onEvent(SignInEvents.OnSubmit)
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
                                text = stringResource(Res.string.sign_in),
                            )
                        }
                    }
                }
            }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.dont_have_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = {
                        viewModel.onEvent(SignInEvents.OnSignUp)
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.sign_up),
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_chevron_right),
                            contentDescription = null,
                        )
                    }
                }
            }

            Text(
                text = "Ver. ${appConfig.versionName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
