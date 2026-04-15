package com.diva.models.auth

data class PasswordResetForm(
    val newPassword: String = "",
    val confirmPassword: String = "",
)
