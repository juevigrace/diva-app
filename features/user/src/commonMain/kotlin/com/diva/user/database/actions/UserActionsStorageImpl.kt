package com.diva.user.database.actions

import com.diva.database.DivaDB
import com.diva.database.user.actions.UserActionsStorage
import com.diva.models.actions.Actions
import com.diva.models.user.actions.UserAction
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseOperation
import io.github.juevigrace.diva.core.errors.NoRowsAffectedException
import io.github.juevigrace.diva.database.DivaDatabase
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserActionsStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserActionsStorage {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getAllByUser(userId: Uuid): Result<List<UserAction>> {
        return db.getList {
            userActionsQueries.findAllByUser(user_id = userId.toString(), mapper = ::mapToEntity)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getAllByUserFlow(userId: Uuid): Flow<Result<List<UserAction>>> {
        return db.getListAsFlow {
            userActionsQueries.findAllByUser(user_id = userId.toString(), mapper = ::mapToEntity)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getOne(id: Uuid): Result<Option<UserAction>> {
        return db.getOne {
            userActionsQueries.findOneById(
                id = id.toString(),
                mapper = ::mapToEntity
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getOneByAction(action: Actions, userId: Uuid): Result<Option<UserAction>> {
        return db.getOne {
            userActionsQueries.findOneByAction(
                user_id = userId.toString(),
                action_name = action,
                mapper = ::mapToEntity
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insert(action: UserAction, userId: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userActionsQueries.insert(
                    id = action.id.toString(),
                    user_id = userId.toString(),
                    action_name = action.action
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.INSERT),
                    table = Option.Some("diva_user_pending_actions"),
                    details = Option.Some("Failed to insert")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertAll(map: Map<Uuid, List<UserAction>>): Result<Unit> {
        for ((key, value) in map) {
            for (action in value) {
                val result = insert(action, key)
                if (result.isFailure) {
                    return result
                }
            }
        }
        return Result.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun delete(id: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userActionsQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_pending_actions"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteByAction(
        action: Actions,
        userId: Uuid
    ): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userActionsQueries.deleteByAction(
                    user_id = userId.toString(),
                    action_name = action
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_pending_actions"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteByUser(userId: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userActionsQueries.deleteByUser(userId.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_pending_actions"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userActionsQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_pending_actions"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun mapToEntity(
        id: String,
        action: Actions
    ): UserAction {
        return UserAction(
            id = Uuid.parse(id),
            action = action,
        )
    }
}
