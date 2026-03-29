package com.diva.user.database.preferences

import com.diva.database.DivaDB
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Theme
import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseAction
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.core.map
import io.github.juevigrace.diva.database.DivaDatabase
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserPreferencesStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserPreferencesStorage {

    override suspend fun getLocal(): DivaResult<Option<UserPreferences>, DivaError> {
        return db.getOne {
            userPreferencesQueries.findLocal(mapper = ::mapToEntity)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getByUser(userId: Uuid): DivaResult<Option<UserPreferences>, DivaError> {
        return db.getOne { userPreferencesQueries.findByUser(userId.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertCloud(
        prefs: UserPreferences,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.insertCloud(
                    id = prefs.id.toString(),
                    user_id = userId.toString(),
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.getOrElse { Clock.System.now() }.toEpochMilliseconds(),
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
    override suspend fun insertLocal(prefs: UserPreferences): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.insert(
                    id = prefs.id.toString(),
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
    override suspend fun insertAll(map: Map<Uuid, List<UserPreferences>>): DivaResult<Unit, DivaError> {
        for ((key, value) in map) {
            for (pref in value) {
                val result = insertCloud(pref, key)
                if (result is DivaResult.Failure) {
                    return result
                }
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun update(prefs: UserPreferences): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.update(
                    id = prefs.id.toString(),
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.getOrElse { null }?.toEpochMilliseconds(),
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
    override suspend fun updateAll(list: List<UserPreferences>): DivaResult<Unit, DivaError> {
        for (pref in list) {
            val result = update(pref)
            if (result is DivaResult.Failure) {
                return result
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateUserId(
        prefId: Uuid,
        userId: Uuid
    ): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.updateUserId(
                    id = prefId.toString(),
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteOne(id: Uuid): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_preferences"),
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
                userPreferencesQueries.deleteByUser(userId.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_preferences"),
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
                userPreferencesQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_user_preferences"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun mapToEntity(
        id: String,
        theme: Theme,
        onboardingCompleted: Boolean,
        language: String,
        lastSyncAt: Long?,
        createdAt: Long,
        updatedAt: Long,
    ): UserPreferences {
        return UserPreferences(
            id = Uuid.parse(id),
            theme = theme,
            onboardingCompleted = onboardingCompleted,
            language = language,
            lastSyncAt = Option.of(lastSyncAt?.let { Instant.fromEpochMilliseconds(it) }),
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt),
        )
    }
}
