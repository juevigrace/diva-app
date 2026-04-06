package com.diva.services

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.me.UserMeRepository
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.launch

interface SyncService : Repository {
    suspend fun sync(onSessionSuccess: () -> Unit, onError: (Throwable) -> Unit)
}

class SyncServiceImpl(
    private val sRepository: SessionRepository,
    private val umRepository: UserMeRepository,
    private val uaRepository: UserActionsRepository,
) : SyncService {
    override suspend fun sync(onSessionSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        sRepository.ping().collect { result ->
            result.fold(
                onFailure = { err -> onError(err) },
                onSuccess = { _ ->
                    scope.launch {
                        umRepository.getMe().collect { result ->
                            result.onFailure { err -> onError(err) }
                        }
                    }
                    scope.launch {
                        uaRepository.syncActions().collect { result ->
                            result.onFailure { err -> onError(err) }
                        }
                    }
                    scope.launch {
                    }
                    onSessionSuccess()
                }
            )
        }
    }
}
