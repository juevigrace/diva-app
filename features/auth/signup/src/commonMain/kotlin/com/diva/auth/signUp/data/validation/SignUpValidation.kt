package com.diva.auth.signUp.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.confirm_password
import com.diva.core.ui.resources.email
import com.diva.core.ui.resources.field_already_taken
import com.diva.core.ui.resources.field_invalid
import com.diva.core.ui.resources.field_min_length
import com.diva.core.ui.resources.field_required
import com.diva.core.ui.resources.password
import com.diva.core.ui.resources.password_mismatch
import com.diva.core.ui.resources.privacy_required
import com.diva.core.ui.resources.terms_required
import com.diva.core.ui.resources.username
import com.diva.models.auth.SignUpForm
import com.diva.models.validation.EmailValidation
import com.diva.models.validation.UsernameValidation
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

object SignUpValidator : Validator<SignUpForm, SignUpValidation> {
    const val PASSWORD_MIN_LENGTH = 4

    override fun validate(form: SignUpForm): SignUpValidation {
        return SignUpValidation(
            emailError = validateEmail(form.email, form.isEmailTaken),
            usernameError = validateUsername(form.username, form.isUsernameTaken),
            passwordError = validatePassword(form.password),
            confirmPasswordError = validateConfirmPassword(form.password, form.confirmPassword),
            termsError = validateTerms(form.termsAndConditions),
            privacyPolicyError = validatePrivacy(form.privacyPolicy),
        )
    }

    private fun validateEmail(email: String, isTaken: Boolean = false): Option<String> {
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
            isTaken -> {
                val value = runBlocking {
                    getString(Res.string.field_already_taken, getString(fieldName))
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }

    private fun validateUsername(username: String, isTaken: Boolean): Option<String> {
        val fieldName = Res.string.username
        return when {
            username.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(fieldName))
                }
                Option.Some(value)
            }
            !UsernameValidation.isValid(username) -> {
                val value = runBlocking {
                    getString(Res.string.field_min_length, getString(fieldName), UsernameValidation.MIN_LENGTH)
                }
                Option.Some(value)
            }
            isTaken -> {
                val value = runBlocking {
                    getString(Res.string.field_already_taken, getString(fieldName))
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
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

    private fun validateTerms(accepted: Boolean): Option<String> {
        return if (!accepted) {
            val value = runBlocking {
                getString(Res.string.terms_required)
            }
            Option.Some(value)
        } else {
            Option.None
        }
    }

    private fun validatePrivacy(accepted: Boolean): Option<String> {
        return if (!accepted) {
            val value = runBlocking {
                getString(Res.string.privacy_required)
            }
            Option.Some(value)
        } else {
            Option.None
        }
    }
}

data class SignUpValidation(
    val emailError: Option<String> = Option.None,
    val showEmailError: Boolean = false,
    val usernameError: Option<String> = Option.None,
    val showUsernameError: Boolean = false,
    val passwordError: Option<String> = Option.None,
    val showPasswordError: Boolean = false,
    val confirmPasswordError: Option<String> = Option.None,
    val showConfirmPasswordError: Boolean = false,
    val termsError: Option<String> = Option.None,
    val showTermsError: Boolean = false,
    val privacyPolicyError: Option<String> = Option.None,
    val showPrivacyPolicyError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showEmailError &&
        showUsernameError &&
        showPasswordError &&
        showConfirmPasswordError &&
        showTermsError &&
        showPrivacyPolicyError

    override fun valid(): Boolean {
        return hasErrors && (
            emailError is Option.None &&
                usernameError is Option.None &&
                passwordError is Option.None &&
                confirmPasswordError is Option.None &&
                termsError is Option.None &&
                privacyPolicyError is Option.None
            )
    }
}
