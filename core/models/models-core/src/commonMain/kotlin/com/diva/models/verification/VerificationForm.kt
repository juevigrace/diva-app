package com.diva.models.verification

import com.diva.models.api.verification.dtos.VerificationDto

data class VerificationForm(
    val token: String = "",
) {
    fun toEmailTokenDto(): VerificationDto {
        return VerificationDto(
            token = token
        )
    }
}
