package com.diva.database.media

import com.diva.models.media.Media
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO: add media-tag functions
interface MediaStorage {
    suspend fun count(): DivaResult<Long, DivaError>

    suspend fun getAll(limit: Int = 100, offset: Int = 0): DivaResult<List<Media>, DivaError>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Media>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Media>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Media>, DivaError>>

    suspend fun insert(item: Media): DivaResult<Unit, DivaError>

    suspend fun update(item: Media): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>
}
