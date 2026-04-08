package com.diva.services

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.me.UserMeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface SyncService : Repository {
    suspend fun sync(): Flow<Result<Unit>>
}

class SyncServiceImpl(
    private val sRepository: SessionRepository,
    private val umRepository: UserMeRepository,
    private val uaRepository: UserActionsRepository,
) : SyncService {
    override suspend fun sync(): Flow<Result<Unit>> {
        return withSession(sRepository::getCurrentFlow) { s ->
            sRepository.ping().collect { result ->
                result.fold(
                    onFailure = { err -> emit(Result.failure(err)) },
                    onSuccess = { _ ->
                        // TODO: fixes
                        scope.launch {
                            umRepository.getMe().collect { result ->
                                result.onFailure { err -> emit(Result.failure(err)) }
                            }
                        }

                        // TODO: user permissions

                        scope.launch {
                            uaRepository.getActions().collect { result ->
                                result.onFailure { err -> emit(Result.failure(err)) }
                            }
                        }

                        emit(Result.success(Unit))
                    }
                )
            }
        }.flowOn(Dispatchers.IO)
    }
}
