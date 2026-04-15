package com.diva.auth.signIn.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.diva.auth.signIn.presentation.events.SignInEvents
import com.diva.auth.signIn.presentation.state.SignInState
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.email_or_username
import com.diva.core.ui.resources.email_placeholder
import com.diva.core.ui.resources.forgot_password
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.ic_lock
import com.diva.core.ui.resources.ic_mail
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_placeholder
import com.diva.core.ui.resources.sign_in
import com.diva.core.ui.resources.success
import com.diva.ui.components.buttons.LoadingButton
import com.diva.ui.components.input.SecureTextField
import com.diva.ui.navigation.arguments.ForgotAction
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInForm(
    modifier: Modifier = Modifier,
    state: SignInState,
    onEvent: (SignInEvents) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.signInForm.username,
            onValueChange = { newValue ->
                onEvent(SignInEvents.OnUsernameChanged(newValue))
            },
            label = {
                Text(text = stringResource(Res.string.email_or_username))
            },
            placeholder = { Text(text = stringResource(Res.string.email_placeholder)) },
            supportingText = when (val text = state.formValidation.usernameError) {
                Option.None -> null
                is Option.Some -> {
                    { Text(text = text.value) }
                }
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_mail),
                    contentDescription = null
                )
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
                    onEvent(SignInEvents.OnPasswordChanged(newValue))
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
                    is Option.Some -> text.value
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
                            onEvent(SignInEvents.OnSubmit)
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
                            onEvent(SignInEvents.OnNavigateToForgot(ForgotAction.Password))
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
        LoadingButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(SignInEvents.OnSubmit)
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
                Text(
                    text = stringResource(Res.string.sign_in),
                )
            }
        }
    }
}
