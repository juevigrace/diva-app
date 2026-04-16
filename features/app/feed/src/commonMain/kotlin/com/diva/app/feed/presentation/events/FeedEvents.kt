package com.diva.app.feed.presentation.events

sealed class FeedEvents {
    data object OnRender : FeedEvents()
}