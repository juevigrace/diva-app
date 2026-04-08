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
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlin.fold
import kotlin.uuid.ExperimentalUuidApi

interface VerificationRepository : Repository {
    fun requestUserVerification(): Flow<Result<Unit>>
    fun requestPasswordReset(email: String): Flow<Result<Unit>>
    fun verify(token: String, action: Actions): Flow<Result<Unit>>
}

class VerificationRepositoryImpl(
    private val api: VerificationApi,
    private val uaRepository: UserActionsStorage,
    private val sRepository: SessionRepository,
) : VerificationRepository {
    override fun requestUserVerification(): Flow<Result<Unit>> {
        return withSession(sRepository::getCurrent) { session ->
            api.requestVerification(RequestVerificationDto(session.user.email, Actions.USER_VERIFICATION.name)).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    override fun requestPasswordReset(email: String): Flow<Result<Unit>> {
        return flow {
            api.requestVerification(RequestVerificationDto(email, Actions.PASSWORD_RESET.name)).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun verify(
        token: String,
        action: Actions
    ): Flow<Result<Unit>> {
        return when (action) {
            Actions.PASSWORD_RESET -> handlePasswordReset(token)
            Actions.USER_VERIFICATION -> handleUserVerification(token)
            Actions.UNKNOWN -> flowOf(
                Result.failure(
                    ConstraintException(
                        field = "action",
                        constraint = "unexpected",
                        value = action.name
                    )
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun handleUserVerification(token: String): Flow<Result<Unit>> {
        return withSession(sRepository::getCurrent) { session ->
            api.verifyWithAuth(
                dto = VerificationDto(token, session.data.toSessionDataDto()),
                token = session.accessToken
            ).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = {
                    uaRepository.deleteByAction(Actions.USER_VERIFICATION, session.user.id).fold(
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = { emit(Result.success(Unit)) }
                    )
                }
            )
        }
    }

    private fun handlePasswordReset(token: String): Flow<Result<Unit>> {
        return flow {
            api.verify<SessionResponse>(VerificationDto(token)).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res ->
                    val session = Session.fromResponse(res)
                    sRepository.newSession(session).collect { result ->
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
