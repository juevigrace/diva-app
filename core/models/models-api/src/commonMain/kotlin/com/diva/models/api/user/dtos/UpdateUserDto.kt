package com.diva.models.api.user.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class UpdateUserDto(
    @SerialName("alias")
    val alias: String = "",
    @SerialName("birth_date")
    val birthDate: Long = Clock.System.now().toEpochMilliseconds(),
    @SerialName("bio")
    val bio: String = "",
    @SerialName("avatar")
    val avatar: String = ""
)
