package com.diva.services

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.me.UserMeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn

data class SyncState(
    val shouldPing: Boolean = false,
)

interface SyncService : Repository {
    fun sync(): Flow<Result<Unit>>
}

class SyncServiceImpl(
    private val sRepository: SessionRepository,
    private val umRepository: UserMeRepository,
    private val uaRepository: UserActionsRepository,
) : SyncService {
    private val state = MutableStateFlow(SyncState())

    override fun sync(): Flow<Result<Unit>> {
        return withSessionObserve(sRepository::observeCurrent) { s ->
            if (state.value.shouldPing) {
                sRepository.ping().onFailure { err ->
                    return@withSessionObserve emit(Result.failure(err))
                }
            }

            // TODO: fetch user
            // TODO: fetch user permissions
            // TODO: fetch user actions
            emit(Result.success(Unit))
        }.flowOn(Dispatchers.IO)
    }
}
