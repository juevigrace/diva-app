package com.diva.app.data

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.services.SyncService
import com.diva.user.data.preferences.UserPreferencesRepository
import io.github.juevigrace.diva.core.errors.ConstraintException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import kotlin.fold

interface AppRepository : Repository {
    fun sync(): Flow<Result<Unit>>
    suspend fun ping(): Result<Unit>
    suspend fun getPreferences(): Result<UserPreferences>
}

class AppRepositoryImpl(
    private val syncService: SyncService,
    private val pRepo: UserPreferencesRepository,
    private val sRepo: SessionRepository,
) : AppRepository {
    override suspend fun getPreferences(): Result<UserPreferences> {
        return pRepo.getUserPreferences().firstOrNull()?.let { res ->
            res.fold(
                onFailure = { err ->
                    if (err is ConstraintException && err.constraint == "missing") {
                        return@let null
                    } else {
                        Result.failure(err)
                    }
                },
                onSuccess = { prefs -> Result.success(prefs) }
            )
        } ?: pRepo.getLocalPreferences()
    }

    override suspend fun ping(): Result<Unit> {
        return sRepo.ping()
    }

    override fun sync(): Flow<Result<Unit>> {
        return callbackFlow {
            val job = scope.launch {
                syncService.sync().collect { res ->
                    trySend(res)
                }
            }

            awaitClose {
                job.cancel()
            }
        }
    }
}
