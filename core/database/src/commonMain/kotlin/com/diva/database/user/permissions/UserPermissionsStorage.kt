package com.diva.database.user.permissions

import com.diva.models.user.permissions.UserPermission
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPermissionsStorage {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun insert(perm: UserPermission, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserPermission>>): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun update(perm: UserPermission, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAll(map: Map<Uuid, List<UserPermission>>): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(permId: Uuid, userId: Uuid): DivaResult<Unit, DivaError>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): DivaResult<Unit, DivaError>

    suspend fun deleteAll(): DivaResult<Unit, DivaError>
}
