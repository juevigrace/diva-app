package com.diva.app.dashboard.presentation.events

sealed class DashboardEvents {
    data object OnRender : DashboardEvents()
}