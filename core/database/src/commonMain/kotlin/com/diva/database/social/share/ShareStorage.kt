package com.diva.database.social.share

import com.diva.models.social.interaction.share.Share
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ShareStorage {
    suspend fun count(): DivaResult<Long, DivaError>

    suspend fun getAll(limit: Int = 100, offset: Int = 0): DivaResult<List<Share>, DivaError>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Share>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Share>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Share>, DivaError>>

    suspend fun insert(item: Share): DivaResult<Unit, DivaError>

    suspend fun update(item: Share): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>
}
