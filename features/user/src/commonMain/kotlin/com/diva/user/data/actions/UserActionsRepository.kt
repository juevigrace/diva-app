package com.diva.user.data.actions

import com.diva.database.session.SessionStorage
import com.diva.database.user.actions.UserActionsStorage
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.actions.safeActionsValueOf
import com.diva.models.api.user.action.event.UserActionsEvents
import com.diva.models.user.actions.UserAction
import com.diva.user.api.client.actions.UserActionsApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserActionsRepository : Repository {
    fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>>
    fun getAction(action: Actions): Flow<DivaResult<Actions, DivaError>>
    fun syncActions(): Flow<DivaResult<Unit, DivaError>>
}

class UserActionsRepositoryImpl(
    private val api: UserActionsApi,
    private val storage: UserActionsStorage,
    private val sessionStorage: SessionStorage,
) : UserActionsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { session ->
            api.getActions(session.accessToken)
                .onSuccess { res ->
                    val actions = res.mapNotNull { actionRes ->
                        val action = safeActionsValueOf(actionRes.actionName)
                        if (action == Actions.UNKNOWN) {
                            return@mapNotNull null
                        }
                        val uAction = UserAction(
                            id = Uuid.parse(actionRes.id),
                            action = action
                        )
                        storage.deleteByAction(
                            action = uAction.action,
                            userId = session.user.id,
                        )
                        uAction
                    }
                    storage.insertAll(mapOf(session.user.id to actions))
                }

            storage.getAllByUser(session.user.id)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { actions ->
                    emit(DivaResult.success(actions.associateBy { it.action }))
                }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getAction(action: Actions): Flow<DivaResult<Actions, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { session ->
            storage.getOneByAction(action, session.user.id)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { option ->
                    option.fold(
                        onNone = {
                            emit(
                                DivaResult.failure(
                                    DivaError(
                                        ErrorCause.Validation.MissingValue(
                                            "action",
                                            details = Option.Some("$action not found")
                                        )
                                    )
                                )
                            )
                        },
                        onSome = { action ->
                            emit(DivaResult.success(action.action))
                        }
                    )
                }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun syncActions(): Flow<DivaResult<Unit, DivaError>> {
        return withSessionFlow(sessionStorage::getCurrentSessionFlow) { session ->
            api.streamActions(session.accessToken).collect { result ->
                result.fold(
                    onFailure = { err -> emit(DivaResult.failure(err)) },
                    onSuccess = { events ->
                        when (events) {
                            is UserActionsEvents.Actions -> {
                                val actions = events.list.mapNotNull { actionRes ->
                                    val action = safeActionsValueOf(actionRes.actionName)
                                    if (action == Actions.UNKNOWN) {
                                        return@mapNotNull null
                                    }
                                    val uAction = UserAction(
                                        id = Uuid.parse(actionRes.id),
                                        action = action
                                    )
                                    storage.deleteByAction(
                                        action = uAction.action,
                                        userId = session.user.id,
                                    )
                                    uAction
                                }
                                storage.insertAll(mapOf(session.user.id to actions))
                            }
                            UserActionsEvents.End -> {
                                emit(DivaResult.success(Unit))
                            }
                        }
                    }
                )
            }
        }
    }
}
