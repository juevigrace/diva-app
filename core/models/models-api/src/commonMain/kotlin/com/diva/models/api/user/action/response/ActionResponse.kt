package com.diva.models.api.user.action.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionResponse(
    @SerialName("id")
    val id: String,
    @SerialName("action_name")
    val actionName: String,
)
