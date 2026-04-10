package com.diva.app.data

import com.diva.models.Repository
import com.diva.models.user.preferences.UserPreferences
import com.diva.services.SyncService
import com.diva.user.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface AppRepository : Repository {
    fun sync(): Flow<Result<Unit>>
    suspend fun getPreferences(): Result<UserPreferences>
}

class AppRepositoryImpl(
    private val syncService: SyncService,
    private val prefRepository: UserPreferencesRepository,
) : AppRepository {
    override suspend fun getPreferences(): Result<UserPreferences> {
        return prefRepository.getLocalPreferences()
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
