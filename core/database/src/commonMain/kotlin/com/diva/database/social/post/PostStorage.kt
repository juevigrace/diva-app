package com.diva.database.social.post

import com.diva.models.social.post.Post
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface PostStorage {
    suspend fun count(): DivaResult<Long, DivaError>

    suspend fun getAll(limit: Int = 100, offset: Int = 0): DivaResult<List<Post>, DivaError>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Post>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Post>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Post>, DivaError>>

    suspend fun insert(item: Post): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>
}
