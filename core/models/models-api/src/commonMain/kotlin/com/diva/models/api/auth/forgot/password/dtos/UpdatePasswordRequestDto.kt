package com.diva.models.api.auth.forgot.password.dtos

import com.diva.models.api.auth.session.dtos.SessionDataDto
import com.diva.models.api.verification.dtos.EmailTokenDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequestDto(
    @SerialName("verification")
    val verification: EmailTokenDto,
    @SerialName("session_data")
    val sessionData: SessionDataDto
)
