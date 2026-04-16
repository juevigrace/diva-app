package com.diva.user.data.preferences

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.user.api.client.preferences.UserPreferencesApi
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintViolationException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.fold
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlin.fold
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPreferencesRepository : Repository {
    suspend fun getLocalPreferences(): Result<UserPreferences>

    fun getUserPreferences(): Flow<Result<UserPreferences>>

    suspend fun createCloudPreferences(prefs: UserPreferences): Result<Unit>

    suspend fun updateCloudPreferences(prefs: UserPreferences): Result<Unit>

    suspend fun updatePreferences(prefs: UserPreferences): Result<Unit>

    suspend fun setUserPreferences(): Result<Unit>
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
                            setUserPreferences().fold(
                                onFailure = { err ->
                                    emit(Result.failure(err))
                                },
                                onSuccess = {
                                    return@fold
                                }
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

    override suspend fun updateCloudPreferences(prefs: UserPreferences): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { s ->
            client.updatePreferences(prefs.toPreferenceDto(), s.accessToken)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updatePreferences(prefs: UserPreferences): Result<Unit> {
        return storage.upsert(prefs.copy(updatedAt = Option.of(Clock.System.now())))
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setUserPreferences(): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { session ->
            val prefs = UserPreferences(id = Uuid.random(), onboardingCompleted = true)
            storage.upsert(prefs).onFailure { err ->
                return@withSession Result.failure(err)
            }
            storage.updateUserId(prefs.id, session.user.id).onFailure { err ->
                if (err is ConstraintViolationException) {
                    return@onFailure
                }
                return@withSession Result.failure(err)
            }
            createCloudPreferences(prefs).onFailure { err ->
                if (err is HttpException && err.statusCode == HttpStatusCode.Unauthorized) {
                    return@onFailure
                }
                return@withSession Result.failure(err)
            }
            Result.success(Unit)
        }
    }
}
