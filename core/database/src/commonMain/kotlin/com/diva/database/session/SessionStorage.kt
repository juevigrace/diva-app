package com.diva.database.session

import com.diva.models.auth.Session
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface SessionStorage {
    suspend fun getAll(limit: Int = 100, offset: Int = 0): DivaResult<List<Session>, DivaError>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Session>, DivaError>>

    suspend fun getCurrentSession(): DivaResult<Option<Session>, DivaError>

    suspend fun getCurrentSessionFlow(): Flow<DivaResult<Option<Session>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Session>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Session>, DivaError>>

    suspend fun insert(item: Session): DivaResult<Unit, DivaError>

    suspend fun insertAll(items: List<Session>): DivaResult<Unit, DivaError>

    suspend fun update(item: Session): DivaResult<Unit, DivaError>

    suspend fun updateAll(items: List<Session>): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateActive(id: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>

    suspend fun deleteAll(): DivaResult<Unit, DivaError>
}
