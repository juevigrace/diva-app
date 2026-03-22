package com.diva.models.auth

import com.diva.models.api.auth.session.dtos.SessionDataDto

data class SessionData(
    val device: String = "",
    val agent: String = "",
    val ip: String = ""
) {
    fun toSessionDataDto(): SessionDataDto {
        return SessionDataDto(
            device = device,
            userAgent = agent
        )
    }
}
