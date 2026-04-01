package com.diva.verification.data

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.actions.UserActionsStorage
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.api.auth.session.response.SessionResponse
import com.diva.models.api.verification.dtos.RequestVerificationDto
import com.diva.models.api.verification.dtos.VerificationDto
import com.diva.models.auth.Session
import com.diva.verification.data.api.client.VerificationApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlin.uuid.ExperimentalUuidApi

interface VerificationRepository : Repository {
    suspend fun requestVerification(email: String, action: Actions): Flow<DivaResult<Unit, DivaError>>
    suspend fun verify(token: String, action: Actions): Flow<DivaResult<Unit, DivaError>>
}

class VerificationRepositoryImpl(
    private val api: VerificationApi,
    private val uaRepository: UserActionsStorage,
    private val sRepository: SessionRepository,
) : VerificationRepository {
    override suspend fun requestVerification(
        email: String,
        action: Actions
    ): Flow<DivaResult<Unit, DivaError>> {
        return flow<DivaResult<Unit, DivaError>> {
            api.requestVerification(RequestVerificationDto(email, action.name))
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess {
                }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun verify(
        token: String,
        action: Actions
    ): Flow<DivaResult<Unit, DivaError>> {
        return when (action) {
            Actions.PASSWORD_RESET -> handlePasswordReset(token)
            Actions.USER_VERIFICATION -> handleUserVerification(token)
            Actions.UNKNOWN -> flowOf(
                DivaResult.failure(
                    DivaError(
                        ErrorCause.Validation.UnexpectedValue(
                            "action",
                            "in Actions enum",
                            action.name,
                        )
                    )
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun handleUserVerification(token: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sRepository::getCurrent) { session ->
            api.verifyWithAuth(
                dto = VerificationDto(token, session.data.toSessionDataDto()),
                token = session.accessToken
            ).fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = {
                    uaRepository.deleteByAction(Actions.USER_VERIFICATION, session.user.id)
                        .onFailure { err -> emit(DivaResult.failure(err)) }
                    emit(DivaResult.success(Unit))
                }
            )
        }
    }

    private fun handlePasswordReset(token: String): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            api.verify<SessionResponse>(VerificationDto(token))
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res ->
                    val session = Session.fromResponse(res)
                    sRepository.newSession(session).collect { result ->
                        result.fold(
                            onFailure = { err -> emit(DivaResult.failure(err)) },
                            onSuccess = { emit(DivaResult.success(Unit)) }
                        )
                    }
                }
        }.flowOn(Dispatchers.IO)
    }
}
