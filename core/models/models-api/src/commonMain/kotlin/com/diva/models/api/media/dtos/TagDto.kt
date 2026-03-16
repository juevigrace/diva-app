package com.diva.models.api.media.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTagDto(
    @SerialName("tag_name")
    val tagName: String,
)

@Serializable
data class UpdateTagDto(
    @SerialName("id")
    val id: String,
    @SerialName("tag_name")
    val tagName: String,
)

