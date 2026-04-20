package com.diva.database.session

import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.Option
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface SessionStorage {
    suspend fun getAll(limit: Int = 100, offset: Int = 0): Result<List<Session>>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<Result<List<Session>>>

    suspend fun getCurrentSession(): Result<Option<Session>>

    suspend fun getCurrentSessionFlow(): Flow<Result<Option<Session>>>

    suspend fun getTemporal(): Result<Option<Session>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): Result<Option<Session>>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<Result<Option<Session>>>

    suspend fun insert(item: Session): Result<Unit>

    suspend fun insertAll(items: List<Session>): Result<Unit>

    suspend fun update(item: Session): Result<Unit>

    suspend fun updateAll(items: List<Session>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateActive(id: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
