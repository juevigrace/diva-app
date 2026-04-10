package com.diva.services

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.me.UserMeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

interface SyncService : Repository {
    suspend fun sync(): Flow<Result<Unit>>
}

class SyncServiceImpl(
    private val sRepository: SessionRepository,
    private val umRepository: UserMeRepository,
    private val uaRepository: UserActionsRepository,
) : SyncService {
    override suspend fun sync(): Flow<Result<Unit>> {
        return withSessionFlow(sRepository::getCurrent) { s ->
            emit(Result.success(Unit))
            // TODO: ping server
            // TODO: fetch user
            // TODO: fetch user permissions
            // TODO: fetch user actions
        }.flowOn(Dispatchers.IO)
    }
}
