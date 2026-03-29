package com.diva.database.user.preferences

import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesStorage {
    suspend fun getLocal(): DivaResult<Option<UserPreferences>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getByUser(userId: Uuid): DivaResult<Option<UserPreferences>, DivaError>

    suspend fun insertLocal(prefs: UserPreferences): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertCloud(prefs: UserPreferences, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserPreferences>>): DivaResult<Unit, DivaError>

    suspend fun update(prefs: UserPreferences): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAll(list: List<UserPreferences>): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateUserId(prefId: Uuid, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteOne(id: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): DivaResult<Unit, DivaError>

    suspend fun deleteAll(): DivaResult<Unit, DivaError>
}
