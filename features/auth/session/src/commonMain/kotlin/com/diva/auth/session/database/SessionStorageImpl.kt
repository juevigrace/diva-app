package com.diva.auth.session.database

import com.diva.database.DivaDB
import com.diva.database.session.SessionStorage
import com.diva.models.auth.Session
import com.diva.models.auth.SessionData
import com.diva.models.session.SessionStatus
import com.diva.models.user.User
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.database.DatabaseAction
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.database.DivaDatabase
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SessionStorageImpl(
    private val db: DivaDatabase<DivaDB>
) : SessionStorage {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCurrentSession(): DivaResult<Option<Session>, DivaError> {
        return db.getOne { sessionQueries.findCurrent(mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCurrentSessionFlow(): Flow<DivaResult<Option<Session>, DivaError>> {
        return db.getOneAsFlow { sessionQueries.findCurrent(mapper = ::mapToEntity) }
    }

    override suspend fun getAll(limit: Int, offset: Int): DivaResult<List<Session>, DivaError> {
        return db.getList { sessionQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    override fun getAllFlow(
        limit: Int,
        offset: Int
    ): Flow<DivaResult<List<Session>, DivaError>> {
        return db.getListAsFlow { sessionQueries.findAll(limit.toLong(), offset.toLong(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: Uuid): DivaResult<Option<Session>, DivaError> {
        return db.getOne { sessionQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Session>, DivaError>> {
        return db.getOneAsFlow { sessionQueries.findOneById(id.toString(), mapper = ::mapToEntity) }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insert(item: Session): DivaResult<Unit, DivaError> {
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
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.INSERT,
                            table = Option.Some("diva_session"),
                            details = Option.Some("Failed to insert")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun insertAll(items: List<Session>): DivaResult<Unit, DivaError> {
        for (item in items) {
            val result = insert(item)
            if (result is DivaResult.Failure) {
                return result
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun update(item: Session): DivaResult<Unit, DivaError> {
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
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_session"),
                            details = Option.Some("Failed to update")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    override suspend fun updateAll(items: List<Session>): DivaResult<Unit, DivaError> {
        for (item in items) {
            val result = update(item)
            if (result is DivaResult.Failure) {
                return result
            }
        }
        return DivaResult.success(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateActive(id: Uuid): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.updateActive(id = id.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.UPDATE,
                            table = Option.Some("diva_session"),
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
                sessionQueries.deleteById(id.toString())
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_session"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
        }
    }

    override suspend fun deleteAll(): DivaResult<Unit, DivaError> {
        return db.use {
            val rows: Long = transactionWithResult {
                sessionQueries.deleteAll()
            }
            if (rows.toInt() == 0) {
                return@use DivaResult.failure(
                    DivaError(
                        ErrorCause.Database.NoRowsAffected(
                            action = DatabaseAction.DELETE,
                            table = Option.Some("diva_session"),
                            details = Option.Some("Failed to delete")
                        )
                    )
                )
            }
            DivaResult.success(Unit)
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
