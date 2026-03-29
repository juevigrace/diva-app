package com.diva.models.validation

object EmailValidation {
    private val emailRegex = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    fun isValid(email: String): Boolean {
        return emailRegex.matches(email)
    }
}
