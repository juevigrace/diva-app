package com.diva.app.data

import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.services.SyncService
import com.diva.user.data.preferences.UserPreferencesRepository
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface AppRepository : Repository {
    fun sync(): Flow<DivaResult<Unit, DivaError>>
    fun getPreferences(): Flow<DivaResult<UserPreferences, DivaError>>
    fun createLocalPreferences(): Flow<DivaResult<Unit, DivaError>>
}

class AppRepositoryImpl(
    private val syncService: SyncService,
    private val prefRepository: UserPreferencesRepository,
) : AppRepository {
    override fun getPreferences(): Flow<DivaResult<UserPreferences, DivaError>> {
        return prefRepository.getLocalPreferences()
    }

    override fun createLocalPreferences(): Flow<DivaResult<Unit, DivaError>> {
        return prefRepository.createLocalPreferences()
    }

    override fun sync(): Flow<DivaResult<Unit, DivaError>> {
        return callbackFlow {
            val job = scope.launch {
                syncService.sync(
                    onSessionSuccess = { trySend(DivaResult.success(Unit)) },
                    onError = { err -> trySend(DivaResult.failure(err)) }
                )
            }

            awaitClose {
                job.cancel()
            }
        }
    }
}
