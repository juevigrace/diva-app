package com.diva.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthLayout(
    content: LazyListScope.() -> Unit
) {
    Screen { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .then(
                        if (maxWidth > 500.dp) {
                            Modifier.widthIn(max = 500.dp).fillMaxHeight()
                        } else {
                            Modifier.fillMaxWidth().fillMaxHeight()
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
            ) {
                item {
                    Image(
                        modifier = Modifier.size(200.dp),
                        painter = painterResource(Res.drawable.puerro),
                        contentDescription = stringResource(Res.string.logo)
                    )
                }
                content()
            }
        }
    }
}
