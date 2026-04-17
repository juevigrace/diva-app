package com.diva.app.profile.presentation.ui.screen

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diva.app.profile.presentation.events.ProfileEvents
import com.diva.app.profile.presentation.viewmodel.ProfileViewModel
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.logout
import com.diva.ui.components.layout.VerticalScrollableLayout
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    VerticalScrollableLayout {
        item {
            TextButton(
                onClick = {
                    viewModel.onEvent(ProfileEvents.SignOut)
                }
            ) {
                Text(text = stringResource(Res.string.logout))
            }
        }
    }
}
