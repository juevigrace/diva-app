package com.diva.auth.forgot.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.api.auth.forgot.password.dtos.UpdatePasswordDto
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.flow.Flow

interface ForgotRepository : Repository {
    fun forgotPasswordReset(newPassword: String): Flow<Result<Unit>>
}

class ForgotRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository
) : ForgotRepository {
    override fun forgotPasswordReset(newPassword: String): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            authClient.forgotPasswordReset(UpdatePasswordDto(newPassword), value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }
}
