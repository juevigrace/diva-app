package com.diva.auth.signUp.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.auth.Session
import com.diva.models.auth.SignUpForm
import io.github.juevigrace.diva.core.fold
import kotlin.uuid.ExperimentalUuidApi

interface SignUpRepository : Repository {
    suspend fun signUp(form: SignUpForm): Result<Unit>
}

class SignUpRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository,
) : SignUpRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun signUp(form: SignUpForm): Result<Unit> {
        return authClient.signUp(form.toSignUpDto()).fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { res -> sessionRepository.newSession(Session.fromResponse(res)) }
        )
    }
}
