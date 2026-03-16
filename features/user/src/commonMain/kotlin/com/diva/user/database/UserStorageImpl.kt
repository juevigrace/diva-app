package com.diva.user.database

import com.diva.database.DivaDB
import com.diva.database.user.UserStorage
import com.diva.models.roles.Role
import com.diva.models.user.User
import com.diva.models.user.permissions.UserPermission
import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseAction
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.database.DivaDatabase
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserStorage {
    override suspend fun count(): DivaResult<Long, DivaError> {
        return db.use { DivaResult.success(userQueries.count().executeAsOne()) }
    }

    override suspend fun getAll(limit: Int, offset: Int): DivaResult<List<User>, DivaError> {
        return db.getList { userQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    override fun getAllFlow(limit: Int, offset: Int): Flow<DivaResult<List<User>, DivaError>> {
        return db.getListAsFlow { userQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: Uuid): DivaResult<Option<User>, DivaError> {
        return db.getOne { userQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<User>, DivaError>> {
        return db.getOneAsFlow { userQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insert(item: User): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.insert(
                    id = item.id.toString(),
                    email = item.email,
                    username = item.username,
                    birth_date = item.birthDate.toEpochMilliseconds(),
                    phone_number = item.phoneNumber,
                    alias = item.alias,
                    avatar = item.avatar,
                    bio = item.bio,
                    role = item.role,
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.INSERT,
                            table = Option.Some("diva_user"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun update(item: User): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.update(
                    email = item.email,
                    username = item.username,
                    alias = item.alias,
                    avatar = item.avatar,
                    bio = item.bio,
                    id = item.id.toString()
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_user"),
                            details = Option.Some("Failed to update")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun delete(id: Uuid): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.delete(id.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insertPermissions(
        userId: Uuid,
        perm: UserPermission
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
    override suspend fun updatePermissions(
        userId: Uuid,
        perm: UserPermission
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deletePermissions(
        userId: Uuid,
        permId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPermissionsQueries.delete(permId.toString(), userId.toString())
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
    override suspend fun findLocalPreferences(): DivaResult<Option<UserPreferences>, DivaError> {
        return db.getOne {
            userPreferencesQueries.findLocal(mapper = { id, _, type, theme, onboardingCompleted, language, lastSyncAt, createdAt, updatedAt ->
                UserPreferences(
                    id = Uuid.parse(id),
                    type = type,
                    theme = theme,
                    onboardingCompleted = onboardingCompleted,
                    language = language,
                    lastSyncAt = Instant.fromEpochMilliseconds(lastSyncAt),
                    createdAt = Instant.fromEpochMilliseconds(createdAt),
                    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
                )
            })
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertCloudPreferences(
        userId: Uuid,
        prefs: UserPreferences
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.insertCloud(
                    id = prefs.id.toString(),
                    user_id = userId.toString(),
                    type = prefs.type,
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.toEpochMilliseconds(),
                    created_at = prefs.createdAt.toEpochMilliseconds(),
                    updated_at = prefs.updatedAt.toEpochMilliseconds(),
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.INSERT,
                            table = Option.Some("diva_user_preferences"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertLocalPreferences(prefs: UserPreferences): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.insert(
                    id = prefs.id.toString(),
                    type = prefs.type,
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.INSERT,
                            table = Option.Some("diva_user_preferences"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updatePreferences(prefs: UserPreferences): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.update(
                    id = prefs.id.toString(),
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.toEpochMilliseconds(),
                    updated_at = prefs.updatedAt.toEpochMilliseconds()
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_user_preferences"),
                            details = Option.Some("Failed to update")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updatePreferenceUserId(
        id: Uuid,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.updateUserId(
                    id = id.toString(),
                    user_id = userId.toString()
                )
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_user_preferences"),
                            details = Option.Some("Failed to update")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    private fun mapToEntity(
        id: String,
        email: String,
        username: String,
        phoneNumber: String,
        birthDate: Long,
        alias: String,
        avatar: String,
        bio: String,
        role: Role,
        createdAt: Long,
        updatedAt: Long,
        deletedAt: Long?,
    ): User {
        return User(
            id = Uuid.parse(id),
            email = email,
            username = username,
            phoneNumber = phoneNumber,
            birthDate = Instant.fromEpochMilliseconds(birthDate),
            alias = alias,
            avatar = avatar,
            bio = bio,
            role = role,
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt),
            deletedAt = Option.of(deletedAt?.let { Instant.fromEpochMilliseconds(it) }),
        )
    }
}
