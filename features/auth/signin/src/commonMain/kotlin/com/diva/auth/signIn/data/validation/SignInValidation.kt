package com.diva.auth.signIn.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.field_required
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.username
import com.diva.models.auth.SignInForm
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

object SignInValidator : Validator<SignInForm, SignInValidation> {
    override fun validate(form: SignInForm): SignInValidation {
        return SignInValidation(
            usernameError = validateUsername(form.username),
            passwordError = validatePassword(form.password),
        )
    }

    private fun validateUsername(username: String): Option<String> {
        return when {
            username.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(Res.string.username))
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }

    private fun validatePassword(password: String): Option<String> {
        if (password.isBlank()) {
            val value = runBlocking {
                getString(Res.string.field_required, getString(Res.string.password))
            }
            return Option.Some(value)
        } else {
            return Option.None
        }
    }
}

data class SignInValidation(
    val usernameError: Option<String> = Option.None,
    val showUsernameError: Boolean = false,
    val passwordError: Option<String> = Option.None,
    val showPasswordError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showUsernameError && showPasswordError

    override fun valid(): Boolean {
        return hasErrors && (usernameError is Option.None && passwordError is Option.None)
    }
}
