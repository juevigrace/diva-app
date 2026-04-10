package com.diva.database.user

import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserStorage {
    suspend fun count(): Result<Long>

    suspend fun getAll(limit: Int = 100, offset: Int = 0): Result<List<User>>

    fun getAllFlow(limit: Int = 100, offset: Int = 0): Flow<Result<List<User>>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getById(id: Uuid): Result<Option<User>>

    @OptIn(ExperimentalUuidApi::class)
    fun getByIdFlow(id: Uuid): Flow<Result<Option<User>>>

    suspend fun upsert(item: User): Result<Unit>

    suspend fun upsertAll(items: List<User>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteAll(): Result<Unit>
}
