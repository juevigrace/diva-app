package com.diva.models.api.user.permissions.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPermissionResponse(
    @SerialName("permission_id")
    val permissionId: String,
    @SerialName("granted_by")
    val grantedBy: String? = null,
    @SerialName("granted")
    val granted: Boolean,
    @SerialName("grantedAt")
    val grantedAt: Long? = null,
    @SerialName("expiresAt")
    val expiresAt: Long? = null,
    @SerialName("updatedAt")
    val updatedAt: Long,
)
