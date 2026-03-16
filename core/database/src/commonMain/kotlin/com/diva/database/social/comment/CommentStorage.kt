package com.diva.database.social.comment

import com.diva.models.social.interaction.comment.Comment
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface CommentStorage {
    suspend fun count(): DivaResult<Long, DivaError>

    suspend fun getAll(limit: Int = 100, offset: Int = 0): DivaResult<List<Comment>, DivaError>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<DivaResult<List<Comment>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): DivaResult<Option<Comment>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<DivaResult<Option<Comment>, DivaError>>

    suspend fun insert(item: Comment): DivaResult<Unit, DivaError>

    suspend fun update(item: Comment): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>
}