package com.diva.auth.forgot.data

import com.diva.auth.data.api.client.AuthApi
import com.diva.auth.session.data.SessionRepository
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.api.auth.forgot.password.dtos.UpdatePasswordDto
import com.diva.models.user.actions.UserAction
import com.diva.ui.navigation.arguments.ForgotAction
import com.diva.user.data.actions.UserActionsRepository
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.util.logError

interface ForgotRepository : Repository {
    suspend fun forgotPasswordReset(newPassword: String): Result<Unit>
    suspend fun checkForAction(action: ForgotAction): Result<UserAction>
}

class ForgotRepositoryImpl(
    private val authClient: AuthApi,
    private val sessionRepository: SessionRepository,
    private val uaRepository: UserActionsRepository,
) : ForgotRepository {
    override suspend fun forgotPasswordReset(newPassword: String): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { session ->
            authClient.forgotPasswordReset(
                dto = UpdatePasswordDto(newPassword = newPassword),
                token = session.accessToken
            ).fold(
                onFailure = { Result.failure(it) },
                onSuccess = {
                    uaRepository.deleteByAction(Actions.PASSWORD_RESET).onFailure { err ->
                        logError(this::class.simpleName ?: "ForgotRepository", err.toString())
                    }

                    sessionRepository.closeCurrent().onFailure { err ->
                        logError(this::class.simpleName ?: "ForgotRepository", err.toString())
                    }
                }
            )
        }
    }

    override suspend fun checkForAction(action: ForgotAction): Result<UserAction> {
        return when (action) {
            ForgotAction.Password, ForgotAction.PasswordWithAuth ->
                uaRepository.getAction(Actions.PASSWORD_RESET)
            ForgotAction.Unspecified -> Result.failure(
                ConstraintException(
                    field = "action",
                    constraint = "missing",
                    value = "unspecified"
                )
            )
        }
    }
}
