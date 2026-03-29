package com.diva.models.user.preferences

import com.diva.models.Theme
import com.diva.models.api.user.preferences.dtos.UserPreferencesDto
import com.diva.models.api.user.preferences.responses.UserPreferencesResponse
import com.diva.models.safeValueOfTheme
import io.github.juevigrace.diva.core.Option
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class UserPreferences(
    val id: Uuid = Uuid.NIL,
    val theme: Theme = Theme.SYSTEM,
    val onboardingCompleted: Boolean = false,
    val language: String = "en",
    val lastSyncAt: Option<Instant> = Option.None,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    fun toPreferenceDto(): UserPreferencesDto {
        return UserPreferencesDto(
            id = id.toString(),
            theme = theme.name,
            onboardingCompleted = onboardingCompleted,
            language = language,
            createdAt = createdAt.toEpochMilliseconds(),
            updatedAt = updatedAt.toEpochMilliseconds()
        )
    }

    companion object {
        fun fromResponse(response: UserPreferencesResponse): UserPreferences {
            return UserPreferences(
                id = Uuid.parse(response.id),
                theme = safeValueOfTheme(response.theme),
                onboardingCompleted = response.onboardingCompleted,
                language = response.language,
                lastSyncAt = Option.of(Instant.fromEpochMilliseconds(response.lastSyncAt)),
                createdAt = Instant.fromEpochMilliseconds(response.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(response.updatedAt)
            )
        }
    }
}
