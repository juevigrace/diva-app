package com.diva.auth.forgot.data

import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.api.auth.forgot.password.dtos.UpdatePasswordDto
import com.diva.models.api.verification.dtos.EmailTokenDto
import com.diva.models.api.user.dtos.UserEmailDto
import com.diva.user.api.client.UserNetworkClient
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ForgotRepository : Repository {
    fun forgotPasswordRequest(email: String): Flow<DivaResult<Unit, DivaError>>

    fun forgotPasswordConfirm(token: String): Flow<DivaResult<Unit, DivaError>>

    fun forgotPasswordReset(newPassword: String): Flow<DivaResult<Unit, DivaError>>
}

class ForgotRepositoryImpl(
    private val userClient: UserNetworkClient,
    private val sessionStorage: SessionStorage
) : ForgotRepository {
    // TODO: handle session if it returns it for the following functions
    override fun forgotPasswordRequest(email: String): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            userClient.forgotPasswordRequest(UserEmailDto(email))
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }.flowOn(Dispatchers.IO)
    }

    override fun forgotPasswordConfirm(token: String): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            userClient.forgotPasswordConfirm(EmailTokenDto(token))
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }.flowOn(Dispatchers.IO)
    }

    override fun forgotPasswordReset(newPassword: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.forgotPasswordReset(UpdatePasswordDto(newPassword), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
