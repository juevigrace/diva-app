package com.diva.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(FlowPreview::class)
@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    var showText by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { text -> { Text(text = text) } },
        placeholder = placeholder?.let { text -> { Text(text = text) } },
        supportingText = supportingText?.let { text -> { Text(text = text) } },
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { showText = !showText }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(
                        if (showText) Res.drawable.ic_eye_off else Res.drawable.ic_eye
                    ),
                    contentDescription = stringResource(
                        if (showText) Res.string.hide_text else Res.string.show_text
                    )
                )
            }
        },
        isError = isError,
        singleLine = true,
        visualTransformation = if (showText) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource
    )
}
