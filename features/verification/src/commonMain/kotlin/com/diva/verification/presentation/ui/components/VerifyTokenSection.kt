package com.diva.verification.presentation.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.verification_code
import com.diva.core.ui.resources.verification_code_description
import com.diva.core.ui.resources.verification_code_placeholder
import com.diva.ui.components.input.LocalTextField
import com.diva.verification.presentation.events.VerificationEvents
import com.diva.verification.presentation.state.VerificationState
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.isPresent
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColumnScope.VerifyTokenSection(
    state: VerificationState,
    onEvent: (VerificationEvents) -> Unit
) {
    Text(
        text = stringResource(Res.string.verification_code_description),
        style = MaterialTheme.typography.bodyLarge,
    )
    LocalTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.verificationForm.token,
        onValueChange = { newValue ->
            onEvent(VerificationEvents.OnTokenChanged(newValue))
        },
        label = stringResource(Res.string.verification_code),
        placeholder = stringResource(Res.string.verification_code_placeholder),
        supportingText = when (val text = state.formValidation.tokenError) {
            Option.None -> null
            is Option.Some -> stringResource(text.value)
        },
        isError = state.formValidation.tokenError.isPresent(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}
