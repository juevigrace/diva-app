package com.diva.app.presentation.state

import com.diva.models.user.preferences.UserPreferences
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class AppState(
    val preferences: UserPreferences = UserPreferences(),
    val prefTries: Int = 0,
    val panic: Boolean = false,
    val shouldNavigate: Boolean = false
)
