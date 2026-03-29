package com.diva.models.validation

object UsernameValidation {
    const val MIN_LENGTH = 3

    fun isValid(username: String): Boolean {
        return username.length >= MIN_LENGTH
    }
}
