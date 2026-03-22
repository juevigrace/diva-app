package com.diva.models.auth

import com.diva.models.api.auth.signup.dto.SignUpDto
import com.diva.models.api.user.dtos.CreateUserDto

data class SignUpForm(
    val email: String = "",
    val username: String = "",
    val alias: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAndConditions: Boolean = false,
    val privacyPolicy: Boolean = false,
    val sessionData: SessionData = SessionData(),
) {
    fun toSignUpDto(): SignUpDto {
        return SignUpDto(
            user = CreateUserDto(
                email = email,
                username = username,
                password = password,
                alias = alias,
            ),
            sessionData = sessionData.toSessionDataDto()
        )
    }
}
