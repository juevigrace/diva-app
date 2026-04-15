package com.diva.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.logo
import com.diva.core.ui.resources.puerro
import com.diva.ui.components.layout.VerticalScrollableLayout
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthLayout(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
    content: LazyListScope.() -> Unit
) {
    VerticalScrollableLayout(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding
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
