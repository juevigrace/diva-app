package com.diva.user.data.actions

import com.diva.auth.session.data.SessionRepository
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
    fun fetchActions(): Flow<DivaResult<Unit, DivaError>>
    fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>>
    fun getAction(action: Actions): Flow<DivaResult<Actions, DivaError>>
    fun syncActions(): Flow<DivaResult<Unit, DivaError>>
}

class UserActionsRepositoryImpl(
    private val api: UserActionsApi,
    private val storage: UserActionsStorage,
    private val sessionRepository: SessionRepository,
) : UserActionsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun fetchActions(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionRepository::getCurrent) { session ->
            api.getActions(session.accessToken).fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = { res ->
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
                    storage.insertAll(mapOf(session.user.id to actions)).fold(
                        onFailure = { err -> emit(DivaResult.failure(err)) },
                        onSuccess = { emit(DivaResult.success(Unit)) }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>> {
        return withSession(sessionRepository::getCurrent) { session ->
            println("Getting actions for $session")
            storage.getAllByUserFlow(session.user.id).collect { result ->
                result.fold(
                    onFailure = { err -> emit(DivaResult.failure(err)) },
                    onSuccess = { actions ->
                        if (actions.isEmpty()) {
                            fetchActions().collect { res ->
                                res.onFailure { err -> emit(DivaResult.failure(err)) }
                            }
                        }
                        emit(DivaResult.success(actions.associateBy { it.action }))
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getAction(action: Actions): Flow<DivaResult<Actions, DivaError>> {
        return withSession(sessionRepository::getCurrent) { session ->
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
        return withSession(sessionRepository::getCurrent) { session ->
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
