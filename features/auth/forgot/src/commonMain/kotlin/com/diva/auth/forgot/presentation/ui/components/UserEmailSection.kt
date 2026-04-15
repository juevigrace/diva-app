package com.diva.auth.forgot.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.diva.auth.forgot.presentation.events.ForgotEvents
import com.diva.auth.forgot.presentation.state.ForgotState
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.email
import com.diva.core.ui.resources.email_placeholder
import com.diva.core.ui.resources.ic_mail
import com.diva.core.ui.resources.password_reset_email_title
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.UserEmailSection(
    state: ForgotState,
    onEvent: (ForgotEvents) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.password_reset_email_title),
        )
    }

    item {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.emailForm.email,
            onValueChange = { newValue ->
                onEvent(ForgotEvents.OnEmailChanged(newValue))
            },
            label = { Text(text = stringResource(Res.string.email)) },
            placeholder = { Text(text = stringResource(Res.string.email_placeholder)) },
            supportingText = when (val text = state.emailFormValidation.emailError) {
                Option.None -> null
                is Option.Some -> { { Text(text = text.value) } }
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_mail),
                    contentDescription = null
                )
            },
            isError = state.emailFormValidation.emailError.isPresent(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )
    }
}
