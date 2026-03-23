package com.diva.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.logo
import com.diva.core.ui.resources.puerro
import io.github.juevigrace.diva.ui.components.layout.Screen
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthLayout(
    content: @Composable () -> Unit
) {
    Screen { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
        ) {
            item {
                Image(
                    modifier = Modifier.size(200.dp),
                    painter = painterResource(Res.drawable.puerro),
                    contentDescription = stringResource(Res.string.logo)
                )
            }
            item {
                content()
            }
        }
    }
}
