package com.diva.auth.session.database

import com.diva.database.DivaDB
import com.diva.database.session.SessionStorage
import com.diva.models.auth.Session
import com.diva.models.auth.SessionData
import com.diva.models.session.SessionStatus
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseOperation
import io.github.juevigrace.diva.core.errors.NoRowsAffectedException
import io.github.juevigrace.diva.core.isEmpty
import io.github.juevigrace.diva.core.map
import io.github.juevigrace.diva.database.DivaDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SessionStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : SessionStorage {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCurrentSession(): Result<Option<Session>> {
        return db.getOne {
            sessionQueries.findCurrentWithUser(mapper = ::mapToEntity)
        }.map { opt ->
            if (opt.isEmpty()) {
                return@map db.getOne {
                    sessionQueries.findCurrent(mapper = ::mapToEntity)
                }.getOrDefault(Option.None)
            } else {
                return@map opt
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCurrentSessionFlow(): Flow<Result<Option<Session>>> {
        return db.getOneAsFlow { sessionQueries.findCurrentWithUser(mapper = ::mapToEntity) }.map { res ->
            res.map { opt ->
                if (opt.isEmpty()) {
                    return@map db.getOne {
                        sessionQueries.findCurrent(mapper = ::mapToEntity)
                    }.getOrDefault(Option.None)
                } else {
                    return@map opt
                }
            }
        }
    }

    override suspend fun getAll(limit: Int, offset: Int): Result<List<Session>> {
        return db.getList { sessionQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    override fun getAllFlow(
        limit: Int,
        offset: Int
    ): Flow<Result<List<Session>>> {
        return db.getListAsFlow { sessionQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: Uuid): Result<Option<Session>> {
        return db.getOne { sessionQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getByIdFlow(id: Uuid): Flow<Result<Option<Session>>> {
        return db.getOneAsFlow { sessionQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insert(item: Session): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.insert(
                    id = item.id.toString(),
                    user_id = item.user.id.toString(),
                    access_token = item.accessToken,
                    refresh_token = item.refreshToken,
                    status = item.status,
                    device = item.data.device,
                    ip_address = item.data.ip,
                    user_agent = item.data.agent,
                    expires_at = item.expiresAt.toEpochMilliseconds(),
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.INSERT),
                    table = Option.Some("diva_session"),
                    details = Option.Some("Failed to insert")
                )
            }
            Result.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insertAll(items: List<Session>): Result<Unit> {
        for (item in items) {
            val result = insert(item)
            if (result.isFailure) {
                return result
            }
        }
        return Result.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun update(item: Session): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.update(
                    id = item.id.toString(),
                    access_token = item.accessToken,
                    refresh_token = item.refreshToken,
                    status = item.status,
                    expires_at = item.expiresAt.toEpochMilliseconds(),
                )
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.UPDATE),
                    table = Option.Some("diva_session"),
                    details = Option.Some("Failed to update")
                )
            }
            Result.success(Unit)
        }
    }

    override suspend fun updateAll(items: List<Session>): Result<Unit> {
        for (item in items) {
            val result = update(item)
            if (result.isFailure) {
                return result
            }
        }
        return Result.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateActive(id: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.updateActive(id = id.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.UPDATE),
                    table = Option.Some("diva_session"),
                    details = Option.Some("Failed to update")
                )
            }
            Result.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun delete(id: Uuid): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_session"),
                    details = Option.Some("Failed to delete")
                )
            }
            Result.success(Unit)
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                throw NoRowsAffectedException(
                    operation = Option.of(DatabaseOperation.DELETE),
                    table = Option.Some("diva_session"),
                    details = Option.Some("Failed to delete")
                )
            }
            Result.success(Unit)
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun mapToEntity(
        id: String,
        accessToken: String,
        refreshToken: String,
        device: String,
        status: SessionStatus,
        ipAddress: String,
        userAgent: String,
        expiresAt: Long,
        createdAt: Long,
        updatedAt: Long,
        userId: String? = null,
        email: String? = null,
        username: String? = null,
        uCreatedAt: Long? = null,
        uUpdatedAt: Long? = null,
    ): Session {
        return Session(
            id = Uuid.parse(id),
            user = User(
                id = userId?.let { Uuid.parse(it) } ?: Uuid.NIL,
                email = email ?: "",
                username = username ?: "",
                createdAt = uCreatedAt?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now(),
                updatedAt = uUpdatedAt?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now(),
            ),
            accessToken = accessToken,
            refreshToken = refreshToken,
            status = status,
            data = SessionData(
                device = device,
                ip = ipAddress,
                agent = userAgent,
            ),
            expiresAt = Instant.fromEpochMilliseconds(expiresAt),
            expired = expiresAt < Clock.System.now().toEpochMilliseconds(),
            createdAt = Instant.fromEpochMilliseconds(createdAt),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt),
        )
    }
}
