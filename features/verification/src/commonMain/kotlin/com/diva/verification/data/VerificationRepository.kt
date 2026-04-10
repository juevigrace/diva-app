package com.diva.verification.data

import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.api.auth.session.response.SessionResponse
import com.diva.models.api.verification.dtos.RequestVerificationDto
import com.diva.models.api.verification.dtos.VerificationDto
import com.diva.models.auth.Session
import com.diva.user.data.actions.UserActionsRepository
import com.diva.verification.data.api.client.VerificationApi
import io.github.juevigrace.diva.core.errors.ConstraintException
import kotlin.fold
import kotlin.uuid.ExperimentalUuidApi

interface VerificationRepository : Repository {
    suspend fun requestUserVerification(): Result<Unit>
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun verify(token: String, action: Actions): Result<Unit>
}

class VerificationRepositoryImpl(
    private val api: VerificationApi,
    private val uaRepository: UserActionsRepository,
    private val sRepository: SessionRepository,
) : VerificationRepository {
    override suspend fun requestUserVerification(): Result<Unit> {
        return withSession(sRepository::getCurrent) { session ->
            api.requestVerification(RequestVerificationDto(session.user.email, Actions.USER_VERIFICATION.name))
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return api.requestVerification(RequestVerificationDto(email, Actions.PASSWORD_RESET.name))
    }

    override suspend fun verify(
        token: String,
        action: Actions
    ): Result<Unit> {
        return when (action) {
            Actions.PASSWORD_RESET -> handlePasswordReset(token)
            Actions.USER_VERIFICATION -> handleUserVerification(token)
            Actions.UNKNOWN -> Result.failure(
                ConstraintException(
                    field = "action",
                    constraint = "unexpected",
                    value = action.name
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleUserVerification(token: String): Result<Unit> {
        return withSession(sRepository::getCurrent) { session ->
            api.verifyWithAuth(
                dto = VerificationDto(token, session.data.toSessionDataDto()),
                token = session.accessToken
            ).onFailure { err -> return@withSession Result.failure(err) }

            uaRepository.deleteByAction(Actions.USER_VERIFICATION)
        }
    }

    private suspend fun handlePasswordReset(token: String): Result<Unit> {
        return api.verify<SessionResponse>(VerificationDto(token)).fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { res ->
                val session = Session.fromResponse(res)
                sRepository.newSession(session).onFailure { err ->
                    return@fold Result.failure(err)
                }
                uaRepository.createAction(Actions.PASSWORD_RESET).onFailure { err ->
                    return@fold Result.failure(err)
                }
                Result.success(Unit)
            }
        )
    }
}
