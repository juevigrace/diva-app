package com.diva.models.verification

import com.diva.models.api.user.dtos.EmailTokenDto

data class VerificationForm(
    val token: String = "",
) {
    fun toEmailTokenDto(): EmailTokenDto {
        return EmailTokenDto(
            token = token
        )
    }
}
