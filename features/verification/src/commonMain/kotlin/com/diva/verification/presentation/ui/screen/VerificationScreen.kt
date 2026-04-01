package com.diva.verification.presentation.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.verification
import com.diva.ui.components.layout.VerticalScrollableLayout
import com.diva.ui.navigation.arguments.VerificationAction
import com.diva.verification.presentation.viewmodel.VerificationViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    action: VerificationAction,
    viewModel: VerificationViewModel = koinViewModel(parameters = { parametersOf(action) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    VerticalScrollableLayout(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.verification))
                }
            )
        },
        contentPadding = PaddingValues(24.dp)
    ) {
        item {

        }
    }
}
