package com.diva.models.api.action.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionResponse(
    @SerialName("action_name")
    val actionName: String,
)