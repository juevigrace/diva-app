package com.diva.ui.components.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.go_back
import com.diva.core.ui.resources.ic_chevron_left
import com.diva.core.ui.resources.puerro
import com.diva.ui.navigation.Destination
import io.github.juevigrace.diva.ui.components.layout.bars.top.TopNavBar
import io.github.juevigrace.diva.ui.navigation.Navigator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorLayout(
    title: String = "",
    message: String,
) {
    val navigator: Navigator<Destination> = koinInject(named("app_router"))
    VerticalScrollableLayout(
        topBar = {
            TopNavBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.pop() }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_left),
                            contentDescription = stringResource(Res.string.go_back)
                        )
                    }
                }
            )
        },
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Image(
                painter = painterResource(Res.drawable.puerro),
                contentDescription = null
            )
        }
        item {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
