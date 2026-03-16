package com.diva.database.session

import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface SessionStorage {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Session>, DivaError>
    suspend fun insert(item: Session): DivaResult<Unit, DivaError>

    suspend fun update(item: Session): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteSessionsByUser(userId: Uuid): DivaResult<Unit, DivaError>

    suspend fun count(): DivaResult<Long, DivaError> {
        return DivaResult.failure(
            DivaError(
                cause = ErrorCause.Error.NotImplemented(
                    Option.Some("database: client action only")
                )
            )
        )
    }

    suspend fun getCurrentSession(): DivaResult<Option<Session>, DivaError> {
        return DivaResult.failure(
            DivaError(
                cause = ErrorCause.Error.NotImplemented(
                    Option.Some("database: client action only")
                )
            )
        )
    }

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Session>, DivaError>> {
        return flow {
            emit(
                DivaResult.failure(
                    DivaError(
                        cause = ErrorCause.Error.NotImplemented(
                            Option.Some("database: client action only")
                        )
                    )
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Session>, DivaError>>{
        return flow {
            emit(
                DivaResult.failure(
                    DivaError(
                        cause = ErrorCause.Error.NotImplemented(
                            Option.Some("database: client action only")
                        )
                    )
                )
            )
        }
    }
}
