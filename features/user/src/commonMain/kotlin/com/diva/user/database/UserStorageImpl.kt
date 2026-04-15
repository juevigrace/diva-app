package com.diva.user.database

import com.diva.database.DivaDB
import com.diva.database.user.UserStorage
import com.diva.models.roles.Role
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseOperation
import io.github.juevigrace.diva.core.errors.ConstraintViolationException
import io.github.juevigrace.diva.core.errors.DuplicateKeyException
import io.github.juevigrace.diva.core.errors.NoRowsAffectedException
import io.github.juevigrace.diva.core.getOrElse
import io.github.juevigrace.diva.database.DivaDatabase
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : UserStorage {
    override suspend fun count(): Result<Long> {
        return db.use { userQueries.count().executeAsOne() }
    }

    override suspend fun getAll(limit: Int, offset: Int): Result<List<User>> {
        return db.getList { userQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    override fun getAllFlow(limit: Int, offset: Int): Flow<Result<List<User>>> {
        return db.getListAsFlow { userQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: Uuid): Result<Option<User>> {
        return db.getOne { userQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getByIdFlow(id: Uuid): Flow<Result<Option<User>>> {
        return db.getOneAsFlow { userQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    private suspend fun insert(item: User): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.insert(
                    id = item.id.toString(),
                    email = item.email,
                    username = item.username,
                    birth_date = item.birthDate.toEpochMilliseconds(),
                    phone_number = item.phoneNumber,
                    user_verified = item.userVerified,
                    alias = item.alias,
                    avatar = item.avatar,
                    bio = item.bio,
                    role = item.role,
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.INSERT),
                    table = Option.Some("diva_user"),
                    details = Option.Some("Failed to insert")
                )
            }
        }
    }

    override suspend fun upsert(item: User): Result<Unit> {
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

    override suspend fun upsertAll(items: List<User>): Result<Unit> {
        for (item in items) {
            val result = upsert(item)
            if (result.isFailure) {
                return result
            }
        }
        return Result.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun update(item: User): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.update(
                    email = item.email,
                    username = item.username,
                    phone_number = item.phoneNumber,
                    birth_date = item.birthDate.toEpochMilliseconds(),
                    user_verified = item.userVerified,
                    role = item.role,
                    updated_at = item.updatedAt.toEpochMilliseconds(),
                    deleted_at = item.deletedAt.getOrElse { null }?.toEpochMilliseconds(),
                    alias = item.alias,
                    avatar = item.avatar,
                    bio = item.bio,
                    id = item.id.toString(),
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.UPDATE),
                    table = Option.Some("diva_user"),
                    details = Option.Some("Failed to update")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun delete(id: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user"),
                    details = Option.Some("Failed to delete")
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteAll(): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                userQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_user"),
                    details = Option.Some("Failed to delete")
                )
            }
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
