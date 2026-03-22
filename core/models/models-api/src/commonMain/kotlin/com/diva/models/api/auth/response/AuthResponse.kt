package com.diva.models.api.auth.response

import com.diva.models.api.action.response.ActionResponse
import com.diva.models.api.auth.session.response.SessionResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("session")
    val session: SessionResponse,
    @SerialName("actions")
    val actions: List<ActionResponse> = emptyList(),
)
