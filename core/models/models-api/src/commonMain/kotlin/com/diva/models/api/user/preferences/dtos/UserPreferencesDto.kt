package com.diva.models.api.user.preferences.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesDto(
    @SerialName("id")
    val id: String,
    @SerialName("theme")
    val theme: String,
    @SerialName("onboarding_completed")
    val onboardingCompleted: Boolean,
    @SerialName("language")
    val language: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long,
)
