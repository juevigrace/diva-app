package com.diva.auth.signIn.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.field_required
import com.diva.models.auth.SignInForm
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import org.jetbrains.compose.resources.StringResource

object SignInValidator : Validator<SignInForm, SignInValidation> {
    override fun validate(form: SignInForm): SignInValidation {
        return SignInValidation(
            usernameError = validateUsername(form.username),
            passwordError = validatePassword(form.password),
        )
    }

    private fun validateUsername(username: String): Option<StringResource> {
        return if (username.isBlank()) {
            Option.Some(Res.string.field_required)
        } else {
            Option.None
        }
    }

    private fun validatePassword(password: String): Option<StringResource> {
        return if (password.isBlank()) {
            Option.Some(Res.string.field_required)
        } else {
            Option.None
        }
    }
}

data class SignInValidation(
    val usernameError: Option<StringResource> = Option.None,
    val showUsernameError: Boolean = false,
    val passwordError: Option<StringResource> = Option.None,
    val showPasswordError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showUsernameError && showPasswordError

    override fun valid(): Boolean {
        return hasErrors && (usernameError is Option.None && passwordError is Option.None)
    }
}
