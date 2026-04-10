package com.diva.auth.signIn.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.auth.Session
import com.diva.models.auth.SignInForm

interface SignInRepository : Repository {
    suspend fun signIn(form: SignInForm): Result<Unit>
}

class SignInRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository,
) : SignInRepository {
    override suspend fun signIn(form: SignInForm): Result<Unit> {
        return authClient.signIn(form.toSignInDto()).fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { res -> sessionRepository.newSession(Session.fromResponse(res)) }
        )
    }
}
