package com.diva.models.auth

import com.diva.models.api.auth.signin.dto.SignInDto

data class SignInForm(
    val username: String = "",
    val password: String = "",
    val sessionData: SessionData = SessionData(),
) {
    fun toSignInDto(): SignInDto {
        return SignInDto(
            username = username,
            password = password,
            sessionData = sessionData.toSessionDataDto()
        )
    }
}
