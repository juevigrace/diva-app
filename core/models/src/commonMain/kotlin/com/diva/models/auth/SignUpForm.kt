package com.diva.models.auth

import com.diva.models.api.auth.dtos.SignUpDto
import com.diva.models.api.user.dtos.CreateUserDto
import kotlin.time.Clock

data class SignUpForm(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: Long = Clock.System.now().toEpochMilliseconds(),
    val phone: String = "",
    val alias: String = "",
    val avatar: String = username,
    val bio: String = "",
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
                birthDate = birthDate,
                phoneNumber = phone,
                alias = alias,
                avatar = avatar,
                bio = bio,
            ),
            sessionData = sessionData.toSessionDataDto()
        )
    }
}
