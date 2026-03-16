package com.diva.models.api.permissions.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePermissionDto(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("level")
    val roleLevel: String
)
