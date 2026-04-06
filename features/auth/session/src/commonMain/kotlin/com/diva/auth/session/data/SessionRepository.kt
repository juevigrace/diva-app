package com.diva.auth.session.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.getOrElse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi

interface SessionRepository : Repository {
    fun getCurrent(): Flow<Result<Session>>
    fun getCurrentFlow(): Flow<Result<Session>>
    fun ping(): Flow<Result<Session>>
    fun logout(): Flow<Result<Unit>>
    fun refresh(): Flow<Result<Unit>>
    fun newSession(session: Session): Flow<Result<Unit>>
}

class SessionRepositoryImpl(
    private val storage: SessionStorage,
    private val api: AuthApi,
) : SessionRepository {
    override fun getCurrent(): Flow<Result<Session>> {
        return flow {
            storage.getCurrentSession().fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { option ->
                    option.fold(
                        onNone = {
                            emit(
                                Result.failure(
                                    ConstraintException(
                                        field = "session",
                                        constraint = "missing",
                                        value = "no session found"
                                    )
                                )
                            )
                        },
                        onSome = { value -> emit(Result.success(value)) }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun getCurrentFlow(): Flow<Result<Session>> {
        return flow {
            storage.getCurrentSessionFlow()
                .collect { result ->
                    result.fold(
                        onFailure = { err ->
                            emit(Result.failure(err))
                        },
                        onSuccess = { option ->
                            option.fold(
                                onNone = {
                                    emit(
                                        Result.failure(
                                            ConstraintException(
                                                field = "session",
                                                constraint = "missing",
                                                value = "no session found"
                                            )
                                        )
                                    )
                                },
                                onSome = { value -> emit(Result.success(value)) }
                            )
                        }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    override fun ping(): Flow<Result<Session>> {
        return withSession(::getCurrent) { session ->
            api.ping(session.accessToken).fold(
                onFailure = { err ->
                    (err.cause as? HttpException)?.let { nErr ->
                        if (nErr.statusCode.getOrElse { null } == 401) {
                            refresh().collect { res ->
                                res.fold(
                                    onFailure = { err -> emit(Result.failure(err)) },
                                    onSuccess = { ping().collect { result -> emit(result) } }
                                )
                            }
                        } else {
                            null
                        }
                    } ?: emit(Result.failure(err))
                },
                onSuccess = {
                    emit(Result.success(session))
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun logout(): Flow<Result<Unit>> {
        return withSession(::getCurrent) { session ->
            api.signOut(session.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { }
            )
            storage.delete(session.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun refresh(): Flow<Result<Unit>> {
        return withSession(::getCurrent) { session ->
            api.refresh(session.data.toSessionDataDto(), session.refreshToken).fold(
                onFailure = { err ->
                    storage.delete(session.id).fold(
                        onFailure = { deleteErr ->
                            return@withSession emit(Result.failure(deleteErr))
                        },
                        onSuccess = { }
                    )
                    emit(Result.failure(err))
                },
                onSuccess = { res ->
                    storage.update(Session.fromResponse(res)).fold(
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = { emit(Result.success(Unit)) }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun newSession(session: Session): Flow<Result<Unit>> {
        return flow {
            storage.insert(session).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = {
                    storage.updateActive(session.id).fold(
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = {
                            emit(Result.success(Unit))
                        }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }
}
