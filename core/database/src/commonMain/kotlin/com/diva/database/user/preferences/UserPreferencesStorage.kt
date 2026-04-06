package com.diva.database.user.preferences

import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.Option
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesStorage {
    suspend fun getLocal(): Result<Option<UserPreferences>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getByUser(userId: Uuid): Result<Option<UserPreferences>>

    suspend fun insertLocal(prefs: UserPreferences): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertCloud(prefs: UserPreferences, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserPreferences>>): Result<Unit>

    suspend fun update(prefs: UserPreferences): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAll(list: List<UserPreferences>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateUserId(prefId: Uuid, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteOne(id: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
