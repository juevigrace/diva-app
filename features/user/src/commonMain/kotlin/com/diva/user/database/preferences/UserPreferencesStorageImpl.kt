package com.diva.user.database.preferences

import com.diva.database.DivaDB
import com.diva.database.user.preferences.UserPreferencesStorage
import com.diva.models.Theme
import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseOperation
import io.github.juevigrace.diva.core.errors.ConstraintViolationException
import io.github.juevigrace.diva.core.errors.DuplicateKeyException
import io.github.juevigrace.diva.core.errors.NoRowsAffectedException
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.database.DivaDatabase
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserPreferencesStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserPreferencesStorage {

    override suspend fun getLocal(): Result<Option<UserPreferences>> {
        return db.getOne { userPreferencesQueries.findLocal(mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getByUser(userId: Uuid): Result<Option<UserPreferences>> {
        return db.getOne { userPreferencesQueries.findByUser(userId.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun insert(prefs: UserPreferences): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.insert(
                    id = prefs.id.toString(),
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.getOrElse { null }?.toEpochMilliseconds(),
                    created_at = prefs.createdAt.getOrElse { Clock.System.now() }.toEpochMilliseconds(),
                    updated_at = prefs.updatedAt.getOrElse { Clock.System.now() }.toEpochMilliseconds(),
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.INSERT),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to insert")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun update(prefs: UserPreferences): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.update(
                    id = prefs.id.toString(),
                    theme = prefs.theme,
                    onboarding_completed = prefs.onboardingCompleted,
                    language = prefs.language,
                    last_sync_at = prefs.lastSyncAt.getOrElse { null }?.toEpochMilliseconds(),
                    updated_at = prefs.updatedAt.getOrElse { Clock.System.now() }.toEpochMilliseconds()
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.UPDATE),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to update")
                )
            }
        }
    }

    override suspend fun upsert(item: UserPreferences): Result<Unit> {
        return insert(item).fold(
            onFailure = { err ->
                if (err is DuplicateKeyException || err is ConstraintViolationException) {
                    update(item)
                } else {
                    Result.failure(err)
                }
            },
            onSuccess = { Result.success(Unit) }
        )
    }

    override suspend fun upsertAll(items: List<UserPreferences>): Result<Unit> {
        for (item in items) {
            val result = upsert(item)
            if (result.isFailure) {
                return result
            }
        }
        return Result.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateUserId(
        prefId: Uuid,
        userId: Uuid
    ): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.updateUserId(
                    id = prefId.toString(),
                    user_id = userId.toString()
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.UPDATE),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to update")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteOne(id: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteByUser(userId: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.deleteByUser(userId.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteAll(): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userPreferencesQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user_preferences"),
                    details = Option.Some("Failed to delete")
                )
            }
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
            createdAt = Option.of(Instant.fromEpochMilliseconds(createdAt)),
            updatedAt = Option.of(Instant.fromEpochMilliseconds(updatedAt)),
        )
    }
}
