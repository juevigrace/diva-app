package com.diva.database.user.permissions

import com.diva.models.user.permissions.UserPermission
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserPermissionsStorage {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun insert(perm: UserPermission, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAll(map: Map<Uuid, List<UserPermission>>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun update(perm: UserPermission, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAll(map: Map<Uuid, List<UserPermission>>): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun delete(permId: Uuid, userId: Uuid): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteByUser(userId: Uuid): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
