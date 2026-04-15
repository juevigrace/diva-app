package com.diva.auth.forgot.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.confirm_password
import com.diva.core.ui.resources.email
import com.diva.core.ui.resources.field_invalid
import com.diva.core.ui.resources.field_min_length
import com.diva.core.ui.resources.field_required
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_mismatch
import com.diva.models.auth.EmailForm
import com.diva.models.auth.PasswordResetForm
import com.diva.models.validation.EmailValidation
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

object EmailRequestValidator : Validator<EmailForm, EmailFormValidation> {
    override fun validate(form: EmailForm): EmailFormValidation {
        return EmailFormValidation(
            emailError = validateEmail(form.email),
        )
    }

    private fun validateEmail(email: String): Option<String> {
        val fieldName = Res.string.email
        return when {
            email.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(fieldName))
                }
                Option.Some(value)
            }
            !EmailValidation.isValid(email) -> {
                val value = runBlocking {
                    getString(Res.string.field_invalid, getString(fieldName), getString(fieldName).lowercase())
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }
}

data class EmailFormValidation(
    val emailError: Option<String> = Option.None,
    val showEmailError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showEmailError

    override fun valid(): Boolean {
        return hasErrors && (emailError is Option.None)
    }
}

object PasswordResetValidator : Validator<PasswordResetForm, PasswordResetValidation> {
    const val PASSWORD_MIN_LENGTH = 4

    override fun validate(form: PasswordResetForm): PasswordResetValidation {
        return PasswordResetValidation(
            passwordError = validatePassword(form.newPassword),
            confirmPasswordError = validateConfirmPassword(form.newPassword, form.confirmPassword),
        )
    }

    private fun validatePassword(password: String): Option<String> {
        val fieldName = Res.string.password
        return when {
            password.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(fieldName))
                }
                Option.Some(value)
            }
            password.length < PASSWORD_MIN_LENGTH -> {
                val value = runBlocking {
                    getString(Res.string.field_min_length, getString(fieldName), PASSWORD_MIN_LENGTH)
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Option<String> {
        val fieldName = Res.string.confirm_password
        return when {
            confirmPassword.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(fieldName))
                }
                Option.Some(value)
            }
            password != confirmPassword -> {
                val value = runBlocking {
                    getString(Res.string.password_mismatch)
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }
}

data class PasswordResetValidation(
    val passwordError: Option<String> = Option.None,
    val showPasswordError: Boolean = false,
    val confirmPasswordError: Option<String> = Option.None,
    val showConfirmPasswordError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showPasswordError && showConfirmPasswordError

    override fun valid(): Boolean {
        return hasErrors && (passwordError is Option.None && confirmPasswordError is Option.None)
    }
}
