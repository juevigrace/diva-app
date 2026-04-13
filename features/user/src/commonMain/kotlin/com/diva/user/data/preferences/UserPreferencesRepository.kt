package com.diva.user.data.preferences

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.user.api.client.preferences.UserPreferencesApi
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.flow.Flow
import kotlin.fold
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesRepository : Repository {
    suspend fun getLocalPreferences(): Result<UserPreferences>

    fun getUserPreferences(): Flow<Result<UserPreferences>>

    suspend fun createCloudPreferences(prefs: UserPreferences): Result<Unit>

    suspend fun updatePreferences(prefs: UserPreferences): Result<Unit>

    suspend fun updateLocalPrefUserId(): Result<Unit>
}

class UserPreferencesRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val storage: UserPreferencesStorage,
    private val client: UserPreferencesApi,
) : UserPreferencesRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getLocalPreferences(): Result<UserPreferences> {
        return storage.getLocal().fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { opt ->
                opt.fold(
                    onNone = {
                        val prefs = UserPreferences(id = Uuid.random())
                        storage.upsert(prefs).onFailure { err ->
                            return@fold Result.failure(err)
                        }
                        return@fold getLocalPreferences()
                    },
                    onSome = { prefs -> Result.success(prefs) }
                )
            }
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getUserPreferences(): Flow<Result<UserPreferences>> {
        // TODO: modify this to fetch from cloud
        return withSessionFlow(sessionRepository::getCurrent) { session ->
            storage.getByUser(session.user.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { opt ->
                    opt.fold(
                        onNone = {
                            emit(
                                Result.failure(
                                    ConstraintException(
                                        field = "preferences",
                                        constraint = "missing",
                                        value = "null"
                                    )
                                )
                            )
                        },
                        onSome = { prefs ->
                            emit(Result.success(prefs))
                        }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createCloudPreferences(prefs: UserPreferences): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { s ->
            client.createPreferences(prefs.toPreferenceDto(), s.accessToken)
        }
    }

    override suspend fun updatePreferences(prefs: UserPreferences): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { s ->
            client.updatePreferences(prefs.toPreferenceDto(), s.accessToken)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateLocalPrefUserId(): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.getByUser(session.user.id).fold(
                onFailure = { err -> Result.failure(err) },
                onSuccess = { opt ->
                    opt.fold(
                        onNone = {
                            Result.failure(
                                ConstraintException(
                                    field = "preferences",
                                    constraint = "missing",
                                    value = "null"
                                )
                            )
                        },
                        onSome = { prefs -> storage.updateUserId(prefs.id, session.user.id) }
                    )
                }
            )
        }
    }
}
