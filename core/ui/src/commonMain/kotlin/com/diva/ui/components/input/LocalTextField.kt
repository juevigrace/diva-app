package com.diva.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.hide_text
import com.diva.core.ui.resources.ic_eye
import com.diva.core.ui.resources.ic_eye_off
import com.diva.core.ui.resources.show_text
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(FlowPreview::class)
@Composable
fun LocalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    debounceMillis: Long = 300L
){
    var hasBeenChanged by remember { mutableStateOf(false) }
    var localValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        if (value != localValue) {
            localValue = value
        }
    }

    LaunchedEffect(localValue) {
        if (hasBeenChanged) {
            snapshotFlow { localValue }
                .debounce(debounceMillis)
                .collect {
                    onValueChange(it)
                }
        }
    }

    OutlinedTextField(
        value = localValue,
        onValueChange = { newValue ->
            localValue = newValue
            hasBeenChanged = true
        },
        modifier = modifier,
        label = label?.let {
            {
                Text(text = it)
            }
        },
        placeholder = placeholder?.let {
            {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
        },
        supportingText = supportingText?.let {
            {
                Text(text = it, style = MaterialTheme.typography.labelMedium)
            }
        },
        prefix = prefix,
        suffix = suffix,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        enabled = enabled,
        readOnly = readOnly
    )
}
