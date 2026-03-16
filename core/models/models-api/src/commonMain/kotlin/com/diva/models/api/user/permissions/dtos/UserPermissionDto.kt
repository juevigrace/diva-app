package com.diva.models.api.user.permissions.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPermissionDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("permission_id")
    val permissionId: String,
    @SerialName("granted")
    val granted: Boolean = false,
    @SerialName("expires_at")
    val expiresAt: Long? = null,
    @SerialName("granted_by")
    val grantedBy: String? = null,
)

@Serializable
data class DeleteUserPermissionDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("permission_id")
    val permissionId: String,
)
