package com.diva.models

import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// TODO: implement events for this?
// TODO: Implement error channel to avoid returning errors directly in the functions
//       and return the values from the cache instead but still log or notify an error
interface Repository {
    val scope: CoroutineScope
        get() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun<T> withSession(
        sessionCall: suspend () -> DivaResult<Option<Session>, DivaError>,
        onFound: suspend FlowCollector<DivaResult<T, DivaError>>.(session: Session) -> Unit
    ): Flow<DivaResult<T, DivaError>> {
        return flow {
            sessionCall()
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { option ->
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
                        onSome = { value -> onFound(value) }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    fun <T> withSessionFlow(
        sessionCall: suspend () -> Flow<DivaResult<Option<Session>, DivaError>>,
        onFound: suspend FlowCollector<DivaResult<T, DivaError>>.(session: Session) -> Unit
    ): Flow<DivaResult<T, DivaError>> {
        return flow {
            sessionCall()
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
                                onSome = { value -> onFound(value) }
                            )
                        }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }
}
