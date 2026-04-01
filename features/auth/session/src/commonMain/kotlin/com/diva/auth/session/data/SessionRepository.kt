package com.diva.auth.session.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.network.HttpStatusCodes
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi

interface SessionRepository : Repository {
    fun getCurrent(): Flow<DivaResult<Session, DivaError>>
    fun getCurrentFlow(): Flow<DivaResult<Session, DivaError>>
    fun ping(): Flow<DivaResult<Session, DivaError>>
    fun logout(): Flow<DivaResult<Unit, DivaError>>
    fun refresh(): Flow<DivaResult<Unit, DivaError>>
    fun newSession(session: Session): Flow<DivaResult<Unit, DivaError>>
}

class SessionRepositoryImpl(
    private val storage: SessionStorage,
    private val api: AuthApi,
) : SessionRepository {
    override fun getCurrent(): Flow<DivaResult<Session, DivaError>> {
        return flow {
            storage.getCurrentSession().fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = { option ->
                    option.fold(
                        onNone = {
                            emit(
                                DivaResult.failure(
                                    DivaError(
                                        cause = ErrorCause.Validation.MissingValue(
                                            field = "session",
                                        )
                                    )
                                )
                            )
                        },
                        onSome = { value -> emit(DivaResult.success(value)) }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun getCurrentFlow(): Flow<DivaResult<Session, DivaError>> {
        return flow {
            storage.getCurrentSessionFlow()
                .collect { result ->
                    result.fold(
                        onFailure = { err ->
                            emit(DivaResult.failure(err))
                        },
                        onSuccess = { option ->
                            option.fold(
                                onNone = {
                                    emit(
                                        DivaResult.failure(
                                            DivaError(
                                                cause = ErrorCause.Validation.MissingValue(
                                                    field = "session",
                                                )
                                            )
                                        )
                                    )
                                },
                                onSome = { value -> emit(DivaResult.success(value)) }
                            )
                        }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    override fun ping(): Flow<DivaResult<Session, DivaError>> {
        return withSession(::getCurrent) { session ->
            api.ping(session.accessToken).fold(
                onFailure = { err ->
                    (err.cause as? ErrorCause.Network.Error)?.let { nErr ->
                        if (nErr.status is HttpStatusCodes.Unauthorized) {
                            refresh().collect { res ->
                                res.fold(
                                    onFailure = { err -> emit(DivaResult.failure(err)) },
                                    onSuccess = { ping().collect { result -> emit(result) } }
                                )
                            }
                        } else {
                            null
                        }
                    } ?: emit(DivaResult.failure(err))
                },
                onSuccess = {
                    emit(DivaResult.success(session))
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun logout(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(::getCurrent) { session ->
            api.signOut(session.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }

            storage.delete(session.id).fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = { emit(DivaResult.success(Unit)) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun refresh(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(::getCurrent) { session ->
            api.refresh(session.data.toSessionDataDto(), session.refreshToken).fold(
                onFailure = { err ->
                    storage.delete(session.id).onFailure { err ->
                        return@withSession emit(DivaResult.failure(err))
                    }
                    emit(DivaResult.failure(err))
                },
                onSuccess = { res ->
                    storage.update(Session.fromResponse(res)).fold(
                        onFailure = { err -> emit(DivaResult.failure(err)) },
                        onSuccess = { emit(DivaResult.success(Unit)) }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun newSession(session: Session): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            storage.insert(session).fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = {
                    storage.updateActive(session.id).fold(
                        onFailure = { err -> emit(DivaResult.failure(err)) },
                        onSuccess = {
                            emit(DivaResult.success(Unit))
                        }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }
}
