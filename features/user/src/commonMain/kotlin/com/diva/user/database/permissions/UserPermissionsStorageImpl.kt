package com.diva.user.database.permissions

import com.diva.database.DivaDB
import com.diva.database.user.permissions.UserPermissionsStorage
import com.diva.models.user.permissions.UserPermission
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseAction
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.database.DivaDatabase
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserPermissionsStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserPermissionsStorage {

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insert(
        perm: UserPermission,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.insert(
                    user_id = userId.toString(),
                    permission_id = perm.permission.id.toString(),
                    granted_at = perm.grantedAt.toEpochMilliseconds(),
                    granted_by = perm.grantedBy.id.toString(),
                    expires_at = perm.expiresAt.getOrElse {
                        Clock.System.now().plus(10.minutes)
                    }.toEpochMilliseconds(),
                    granted = perm.granted,
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.INSERT,
                            table = Option.Some("diva_user_permissions"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insertAll(map: Map<Uuid, List<UserPermission>>): DivaResult<Unit, DivaError> {
        for ((key, value) in map) {
            for (perm in value) {
                val result = insert(perm, key)
                if (result is DivaResult.Failure) {
                    return result
                }
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun update(
        perm: UserPermission,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.update(
                    granted = perm.granted,
                    expires_at = perm.expiresAt.getOrElse {
                        Clock.System.now().plus(10.minutes)
                    }.toEpochMilliseconds(),
                    user_id = userId.toString(),
                    permission_id = perm.permission.id.toString(),
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_user_permissions"),
                            details = Option.Some("Failed to update")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun updateAll(map: Map<Uuid, List<UserPermission>>): DivaResult<Unit, DivaError> {
        for ((key, value) in map) {
            for (perm in value) {
                val result = update(perm, key)
                if (result is DivaResult.Failure) {
                    return result
                }
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun delete(
        permId: Uuid,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.deleteById(permId.toString(), userId.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_permissions"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteByUser(userId: Uuid): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.deleteByUser(userId.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_permissions"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteAll(): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_permissions"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }
}
