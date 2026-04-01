package com.diva.database.user.actions

import com.diva.models.actions.Actions
import com.diva.models.user.actions.UserAction
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserActionsStorage {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getAllByUser(userId: Uuid): DivaResult<List<UserAction>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    fun getAllByUserFlow(userId: Uuid): Flow<DivaResult<List<UserAction>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getOne(id: Uuid): DivaResult<Option<UserAction>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getOneByAction(action: Actions, userId: Uuid): DivaResult<Option<UserAction>, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insert(action: UserAction, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserAction>>): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(id: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByAction(action: Actions, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): DivaResult<Unit, DivaError>

    suspend fun deleteAll(): DivaResult<Unit, DivaError>
}
