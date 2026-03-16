package com.diva.models.api.permissions.response

import kotlinx.serialization.Serializable

@Serializable
data class PermissionResponse(
    val id: String,
    val name: String,
    val description: String,
    val resource: String,
    val action: String,
    val createdAt: String,
    val updatedAt: String,
)