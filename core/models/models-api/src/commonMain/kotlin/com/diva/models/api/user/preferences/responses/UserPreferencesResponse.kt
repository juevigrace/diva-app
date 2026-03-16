package com.diva.models.api.user.preferences.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesResponse(
    @SerialName("id")
    val id: String,
    @SerialName("theme")
    val theme: String,
    @SerialName("onboarding_completed")
    val onboardingCompleted: Boolean,
    @SerialName("language")
    val language: String,
    @SerialName("last_sync_at")
    val lastSyncAt: Long,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)
