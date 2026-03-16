package com.diva.onboarding.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.app
import com.diva.core.ui.resources.puerro
import io.github.juevigrace.diva.ui.components.layout.Screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen() {
    Screen { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(Res.drawable.puerro),
                contentDescription = stringResource(Res.string.app)
            )
        }
    }
}
