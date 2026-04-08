package com.diva.auth.signIn.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.auth.Session
import com.diva.models.auth.SignInForm
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

interface SignInRepository {
    fun signIn(form: SignInForm): Flow<Result<Unit>>
}

class SignInRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository,
) : SignInRepository {
    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    override fun signIn(form: SignInForm): Flow<Result<Unit>> {
        return flow {
            authClient.signIn(form.toSignInDto()).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res ->
                    val session = Session.fromResponse(res)
                    sessionRepository.newSession(session).collect { result ->
                        result.fold(
                            onFailure = { err -> emit(Result.failure(err)) },
                            onSuccess = { emit(Result.success(Unit)) }
                        )
                    }
                }
            )
        }.flowOn(Dispatchers.IO)
    }
}
