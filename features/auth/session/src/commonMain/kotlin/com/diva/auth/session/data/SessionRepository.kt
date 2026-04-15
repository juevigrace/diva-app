package com.diva.auth.session.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.core.isEmpty
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.fold
import kotlin.uuid.ExperimentalUuidApi

interface SessionRepository : Repository {
    suspend fun getCurrent(): Result<Session>
    fun observeCurrent(): Flow<Result<Session>>
    suspend fun ping(): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun closeCurrent(): Result<Unit>
    suspend fun refresh(): Result<Unit>
    suspend fun newSession(session: Session): Result<Unit>
}

class SessionRepositoryImpl(
    private val storage: SessionStorage,
    private val api: AuthApi,
) : SessionRepository {
    override suspend fun getCurrent(): Result<Session> {
        return storage.getCurrentSession().fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { option ->
                option.fold(
                    onNone = {
                        Result.failure(
                            ConstraintException(
                                field = "session",
                                constraint = "missing",
                                value = "no session found"
                            )
                        )
                    },
                    onSome = { value -> Result.success(value) }
                )
            }
        )
    }

    override fun observeCurrent(): Flow<Result<Session>> {
        return flow {
            storage.getCurrentSessionFlow().collect { result ->
                result.fold(
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
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun ping(): Result<Unit> {
        return withSession(::getCurrent) { session ->
            api.ping(session.accessToken).fold(
                onFailure = { err ->
                    (err as? HttpException)?.let { ex ->
                        if (ex.statusCode.isEmpty()) {
                            return@let null
                        }

                        when ((ex.statusCode as Option.Some).value) {
                            HttpStatusCode.Unauthorized.value -> {
                                return@fold refresh()
                            }
                            else -> null
                        }
                    } ?: Result.failure(err)
                },
                onSuccess = { Result.success(Unit) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun logout(): Result<Unit> {
        return withSession(::getCurrent) { session ->
            api.signOut(session.accessToken).onFailure { err ->
                if (err is HttpException &&
                    err.statusCode.getOrElse {
                        HttpStatusCode.InternalServerError
                    } == HttpStatusCode.Unauthorized.value
                ) {
                    return@onFailure
                }

                return@withSession Result.failure(err)
            }
            closeCurrent()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun closeCurrent(): Result<Unit> {
        return withSession(::getCurrent) { session ->
            storage.delete(session.id).onFailure { err ->
                return@withSession Result.failure(err)
            }

            Result.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun refresh(): Result<Unit> {
        return withSession(::getCurrent) { session ->
            api.refresh(session.data.toSessionDataDto(), session.refreshToken).fold(
                onFailure = { err ->
                    (err as? HttpException)?.let { ex ->
                        if (ex.statusCode.isEmpty()) {
                            return@let null
                        }

                        when ((ex.statusCode as Option.Some).value) {
                            HttpStatusCode.Unauthorized.value -> {
                                storage.delete(session.id).onSuccess {
                                    return@let Result.failure(
                                        ConstraintException(
                                            field = "session",
                                            constraint = "expired",
                                            value = "no session found"
                                        )
                                    )
                                }
                            }
                            else -> null
                        }
                    } ?: Result.failure(err)
                },
                onSuccess = { res -> storage.update(Session.fromResponse(res)) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun newSession(session: Session): Result<Unit> {
        return storage.insert(session).fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { storage.updateActive(session.id) }
        )
    }
}
