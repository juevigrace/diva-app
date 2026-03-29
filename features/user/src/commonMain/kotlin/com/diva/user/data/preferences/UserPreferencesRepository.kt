package com.diva.user.data.preferences

import com.diva.database.session.SessionStorage
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.user.api.client.preferences.UserPreferencesApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesRepository : Repository {
    fun getLocalPreferences(): Flow<DivaResult<UserPreferences, DivaError>>

    fun createLocalPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>>

    fun createCloudPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>>

    fun updatePreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateLocalPrefUserId(id: Uuid): Flow<DivaResult<Unit, DivaError>>

    fun syncPreferences(): Flow<DivaResult<Unit, DivaError>>
}

class UserPreferencesRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val storage: UserPreferencesStorage,
    private val client: UserPreferencesApi,
) : UserPreferencesRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun getLocalPreferences(): Flow<DivaResult<UserPreferences, DivaError>> {
        return flow {
            storage.getLocal()
                .onFailure { emit(DivaResult.failure(it)) }
                .onSuccess { opt ->
                    opt.fold(
                        onNone = {
                            emit(DivaResult.failure(DivaError(ErrorCause.Validation.MissingValue("preferences"))))
                        },
                        onSome = { prefs ->
                            emit(DivaResult.success(prefs))
                        }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun createLocalPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            storage.insertLocal(prefs)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess {
                    emit(DivaResult.success(Unit))
                }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun createCloudPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { session ->
            storage.insertCloud(prefs, session.user.id)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun updatePreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            storage.update(prefs)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun updateLocalPrefUserId(id: Uuid): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            storage.updateUserId(id, value.user.id)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun syncPreferences(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            client.updatePreferences(value.user.preferences.toPreferenceDto(), value.accessToken)
                .onFailure { err ->
                    // TODO: check in api which error is returned, if not able to update because
                    // preferences are not found then create them
                    if (err.cause is ErrorCause.Validation.MissingValue) {
                        client.createPreferences(value.user.preferences.toPreferenceDto(), value.accessToken)
                            .onFailure { err -> emit(DivaResult.failure(err)) }
                            .onSuccess { emit(DivaResult.success(Unit)) }
                    }
                }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
