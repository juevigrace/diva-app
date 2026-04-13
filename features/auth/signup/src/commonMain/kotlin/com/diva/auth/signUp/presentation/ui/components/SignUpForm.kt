package com.diva.auth.signUp.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.diva.auth.signUp.presentation.events.SignUpEvents
import com.diva.auth.signUp.presentation.state.SignUpState
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.by_continuing_for_privacy
import com.diva.core.ui.resources.by_continuing_for_temrs
import com.diva.core.ui.resources.confirm_password
import com.diva.core.ui.resources.confirm_password_placeholder
import com.diva.core.ui.resources.email
import com.diva.core.ui.resources.email_placeholder
import com.diva.core.ui.resources.ic_circle_check
import com.diva.core.ui.resources.ic_lock
import com.diva.core.ui.resources.ic_mail
import com.diva.core.ui.resources.ic_user
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_placeholder
import com.diva.core.ui.resources.privacy_policy
import com.diva.core.ui.resources.sign_up
import com.diva.core.ui.resources.success
import com.diva.core.ui.resources.terms_and_conditions
import com.diva.core.ui.resources.username
import com.diva.core.ui.resources.username_placeholder
import com.diva.ui.components.input.LocalTextField
import com.diva.ui.components.input.SecureTextField
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    state: SignUpState,
    onEvent: (SignUpEvents) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalTextField(
            modifier = Modifier.fillMaxWidth(),
            debounceMillis = 600L,
            value = state.signUpForm.email,
            onValueChange = { newValue ->
                onEvent(SignUpEvents.OnEmailChanged(newValue))
            },
            label = stringResource(Res.string.email),
            placeholder = stringResource(Res.string.email_placeholder),
            supportingText = when (val text = state.formValidation.emailError) {
                Option.None -> null
                is Option.Some -> text.value
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_mail),
                    contentDescription = null
                )
            },
            isError = state.formValidation.emailError.isPresent(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )

        LocalTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.signUpForm.username,
            debounceMillis = 600L,
            onValueChange = { newValue ->
                onEvent(SignUpEvents.OnUsernameChanged(newValue))
            },
            label = stringResource(Res.string.username),
            placeholder = stringResource(Res.string.username_placeholder),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_user),
                    contentDescription = null
                )
            },
            supportingText = when (val text = state.formValidation.usernameError) {
                Option.None -> null
                is Option.Some -> text.value
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

        SecureTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.signUpForm.password,
            onValueChange = { newValue ->
                onEvent(SignUpEvents.OnPasswordChanged(newValue))
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
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        SecureTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.signUpForm.confirmPassword,
            onValueChange = { newValue ->
                onEvent(SignUpEvents.OnConfirmPasswordChanged(newValue))
            },
            label = stringResource(Res.string.confirm_password),
            placeholder = stringResource(Res.string.confirm_password_placeholder),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_lock),
                    contentDescription = null
                )
            },
            supportingText = when (val text = state.formValidation.confirmPasswordError) {
                Option.None -> null
                is Option.Some -> text.value
            },
            isError = state.formValidation.confirmPasswordError.isPresent(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .clickable(
                    onClick = { onEvent(SignUpEvents.ToggleTerms) },
                    role = Role.Button
                )
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.signUpForm.termsAndConditions,
                onCheckedChange = { onEvent(SignUpEvents.ToggleTerms) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${stringResource(Res.string.by_continuing_for_temrs)} ",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    modifier = Modifier.clickable(
                        onClick = { println("Show Terms") },
                        role = Role.Button
                    ),
                    text = stringResource(Res.string.terms_and_conditions),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .clickable(
                    onClick = { onEvent(SignUpEvents.TogglePrivacyPolicy) },
                    role = Role.Button
                )
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.signUpForm.privacyPolicy,
                onCheckedChange = { onEvent(SignUpEvents.TogglePrivacyPolicy) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${stringResource(Res.string.by_continuing_for_privacy)} ",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    modifier = Modifier.clickable(
                        onClick = { println("Show Privacy") },
                        role = Role.Button
                    ),
                    text = stringResource(Res.string.privacy_policy),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                )
            }
        }

        // TODO: create a button component for this
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.submitEnabled,
            colors = ButtonDefaults.buttonColors(),
            onClick = {
                onEvent(SignUpEvents.OnSubmit)
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
                        text = stringResource(Res.string.sign_up),
                    )
                }
            }
        }
    }
}
