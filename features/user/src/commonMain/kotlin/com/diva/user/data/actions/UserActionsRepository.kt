package com.diva.user.data.actions

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.actions.UserActionsStorage
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.actions.safeActionsValueOf
import com.diva.models.api.user.action.event.UserActionsEvents
import com.diva.models.user.actions.UserAction
import com.diva.user.api.client.actions.UserActionsApi
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserActionsRepository : Repository {
    fun fetchActions(): Flow<Result<Unit>>
    fun getActions(): Flow<Result<Map<Actions, UserAction>>>
    fun getAction(action: Actions): Flow<Result<Actions>>
    fun syncActions(): Flow<Result<Unit>>
}

class UserActionsRepositoryImpl(
    private val api: UserActionsApi,
    private val storage: UserActionsStorage,
    private val sessionRepository: SessionRepository,
) : UserActionsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override fun fetchActions(): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { session ->
            api.getActions(session.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
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
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = { emit(Result.success(Unit)) }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getActions(): Flow<Result<Map<Actions, UserAction>>> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.getAllByUserFlow(session.user.id).collect { result ->
                result.fold(
                    onFailure = { err -> emit(Result.failure(err)) },
                    onSuccess = { actions ->
                        if (actions.isEmpty()) {
                            fetchActions().collect { res ->
                                res.fold(
                                    onFailure = { err -> emit(Result.failure(err)) },
                                    onSuccess = { return@collect }
                                )
                            }
                        }
                        emit(Result.success(actions.associateBy { it.action }))
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getAction(action: Actions): Flow<Result<Actions>> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.getOneByAction(action, session.user.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { option ->
                    option.fold(
                        onNone = {
                            emit(
                                Result.failure(
                                    ConstraintException(
                                        field = "action",
                                        constraint = "missing",
                                        value = "$action not found"
                                    )
                                )
                            )
                        },
                        onSome = { action ->
                            emit(Result.success(action.action))
                        }
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun syncActions(): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { session ->
            api.streamActions(session.accessToken).collect { result ->
                result.fold(
                    onFailure = { err -> emit(Result.failure(err)) },
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
                                emit(Result.success(Unit))
                            }
                        }
                    }
                )
            }
        }
    }
}
