package com.diva.user.data.preferences

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.user.api.client.preferences.UserPreferencesApi
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesRepository : Repository {
    fun getLocalPreferences(): Flow<Result<UserPreferences>>

    fun createLocalPreferences(): Flow<Result<Unit>>

    fun getUserPreferences(): Flow<Result<UserPreferences>>

    fun createCloudPreferences(prefs: UserPreferences): Flow<Result<Unit>>

    fun updatePreferences(prefs: UserPreferences): Flow<Result<Unit>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateLocalPrefUserId(id: Uuid): Flow<Result<Unit>>

    fun syncPreferences(): Flow<Result<Unit>>
}

class UserPreferencesRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val storage: UserPreferencesStorage,
    private val client: UserPreferencesApi,
) : UserPreferencesRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun getLocalPreferences(): Flow<Result<UserPreferences>> {
        return flow {
            storage.getLocal().fold(
                onFailure = { emit(Result.failure(it)) },
                onSuccess = { opt ->
                    opt.fold(
                        onNone = {
                            emit(Result.failure(ConstraintException(
                                field = "preferences",
                                constraint = "missing",
                                value = "no preferences found"
                            )))
                        },
                        onSome = { prefs ->
                            emit(Result.success(prefs))
                        }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun createLocalPreferences(): Flow<Result<Unit>> {
        return flow {
            val prefs = UserPreferences(id = Uuid.random())
            storage.insertLocal(prefs).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = {
                    emit(Result.success(Unit))
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun getUserPreferences(): Flow<Result<UserPreferences>> {
        return withSession(sessionRepository::getCurrent) { }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun createCloudPreferences(prefs: UserPreferences): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.insertCloud(prefs, session.user.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    override fun updatePreferences(prefs: UserPreferences): Flow<Result<Unit>> {
        return flow {
            storage.update(prefs).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun updateLocalPrefUserId(id: Uuid): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            storage.updateUserId(id, value.user.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    override fun syncPreferences(): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            client.updatePreferences(value.user.preferences.toPreferenceDto(), value.accessToken).fold(
                onFailure = { err ->
                    if (err.cause is ConstraintException) {
                        client.createPreferences(value.user.preferences.toPreferenceDto(), value.accessToken).fold(
                            onFailure = { err -> emit(Result.failure(err)) },
                            onSuccess = { emit(Result.success(Unit)) }
                        )
                    } else {
                        emit(Result.failure(err))
                    }
                },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }
}
