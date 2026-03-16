package com.diva.auth.forgot.presentation.state

data class ForgotState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val emailError: String? = null,
    val tokenError: String? = null,
    val passwordError: String? = null,
    val success: Boolean = false,
)
