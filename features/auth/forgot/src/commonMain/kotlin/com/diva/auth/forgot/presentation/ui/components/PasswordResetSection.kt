package com.diva.auth.forgot.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.confirm_password
import com.diva.core.ui.resources.confirm_password_placeholder
import com.diva.core.ui.resources.ic_lock
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_placeholder
import com.diva.ui.components.input.SecureTextField
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.PasswordResetSection(
    state: ForgotState,
    onEvent: (ForgotEvents) -> Unit
) {
    item {
        val focusManager = LocalFocusManager.current
        SecureTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.passwordResetForm.newPassword,
            onValueChange = { newValue ->
                onEvent(ForgotEvents.OnNewPasswordChanged(newValue))
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
            supportingText = when (val text = state.passwordResetValidation.passwordError) {
                Option.None -> null
                is Option.Some -> text.value
            },
            isError = state.passwordResetValidation.passwordError.isPresent(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
    }

    item {
        val focusManager = LocalFocusManager.current
        SecureTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.passwordResetForm.confirmPassword,
            onValueChange = { newValue ->
                onEvent(ForgotEvents.OnConfirmPasswordChanged(newValue))
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
            supportingText = when (val text = state.passwordResetValidation.confirmPasswordError) {
                Option.None -> null
                is Option.Some -> text.value
            },
            isError = state.passwordResetValidation.confirmPasswordError.isPresent(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
    }
}
