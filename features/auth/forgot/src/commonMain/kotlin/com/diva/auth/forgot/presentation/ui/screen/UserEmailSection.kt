package com.diva.auth.forgot.presentation.ui.screen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState

@Composable
fun ColumnScope.UserEmailSection(
    state: ForgotState,
    onEvent: (ForgotEvents) -> Unit
) {
    /*Text(
        text = stringResource(Res.string.password_reset_email_title),
        style = MaterialTheme.typography.bodyLarge,
    )
    LocalTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.verificationForm.token,
        onValueChange = { newValue ->
            onEvent(VerificationEvents.OnTokenChanged(newValue))
        },
        label = stringResource(Res.string.email),
        placeholder = stringResource(Res.string.email_placeholder),
        supportingText = when (val text = state.formValidation.tokenError) {
            Option.None -> null
            is Option.Some -> stringResource(text.value)
        },
        isError = state.formValidation.tokenError.isPresent(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
    )*/
}
