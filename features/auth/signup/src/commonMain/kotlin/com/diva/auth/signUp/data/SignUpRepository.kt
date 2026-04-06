package com.diva.auth.signUp.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.auth.Session
import com.diva.models.auth.SignUpForm
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi

interface SignUpRepository : Repository {
    fun signUp(form: SignUpForm): Flow<Result<Unit>>
}

class SignUpRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionStorage: SessionStorage,
) : SignUpRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun signUp(form: SignUpForm): Flow<Result<Unit>> {
        return flow {
            authClient.signUp(form.toSignUpDto()).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res ->
                    val session = Session.fromResponse(res)
                    sessionStorage
                        .insert(session)
                        .fold(
                            onFailure = { err -> emit(Result.failure(err)) },
                            onSuccess = {
                                sessionStorage.update(session.copy(isCurrent = true)).fold(
                                    onFailure = { err ->
                                        sessionStorage.delete(session.id).fold(
                                            onFailure = { deleteErr -> println("panik: ${deleteErr.message}") },
                                            onSuccess = { }
                                        )
                                        emit(Result.failure(err))
                                    },
                                    onSuccess = { emit(Result.success(Unit)) }
                                )
                            }
                        )
                }
            )
        }.flowOn(Dispatchers.Default)
    }
}
