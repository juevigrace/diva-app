package com.diva.auth.forgot.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.api.auth.forgot.password.dtos.UpdatePasswordDto
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.flow.Flow

interface ForgotRepository : Repository {
    fun forgotPasswordReset(newPassword: String): Flow<DivaResult<Unit, DivaError>>
}

class ForgotRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository
) : ForgotRepository {
    override fun forgotPasswordReset(newPassword: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionRepository::getCurrent) { value ->
            authClient.forgotPasswordReset(UpdatePasswordDto(newPassword), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
