package com.diva.app.home.presentation.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable

@Composable
fun HomeDrawer(
    content: @Composable () -> Unit,
    drawerContent: @Composable ColumnScope.() -> Unit,
){
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet(
                        drawerState = drawerState,
                        content = drawerContent
                    )
                },
                drawerState = drawerState,
                content = content
            )
}
