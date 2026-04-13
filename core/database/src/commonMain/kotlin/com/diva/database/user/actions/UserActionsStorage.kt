package com.diva.database.user.actions

import com.diva.models.actions.Actions
import com.diva.models.user.actions.UserAction
import io.github.juevigrace.diva.core.Option
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserActionsStorage {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getAllByUser(userId: Uuid): Result<List<UserAction>>

    @OptIn(ExperimentalUuidApi::class)
    fun getAllByUserFlow(userId: Uuid): Flow<Result<List<UserAction>>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getOne(id: Uuid): Result<Option<UserAction>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getOneByAction(action: Actions, userId: Uuid): Result<Option<UserAction>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insert(action: UserAction, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserAction>>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
