package com.diva.auth.forgot.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository

interface ForgotRepository : Repository {
    suspend fun forgotPasswordReset(newPassword: String): Result<Unit>
}

class ForgotRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository
) : ForgotRepository {
    override suspend fun forgotPasswordReset(newPassword: String): Result<Unit> {
        TODO()
    }
}
