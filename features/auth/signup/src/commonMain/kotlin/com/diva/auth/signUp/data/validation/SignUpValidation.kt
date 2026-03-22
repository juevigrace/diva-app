package com.diva.auth.signUp.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.field_required
import com.diva.core.ui.resources.invalid_phone_number
import com.diva.core.ui.resources.password_mismatch
import com.diva.core.ui.resources.password_too_short
import com.diva.core.ui.resources.privacy_required
import com.diva.core.ui.resources.terms_required
import com.diva.models.auth.SignUpForm
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import org.jetbrains.compose.resources.StringResource

object SignUpValidator : Validator<SignUpForm, SignUpValidation> {
    override fun validate(form: SignUpForm): SignUpValidation {
        return SignUpValidation(
            emailError = validateEmail(form.email),
            usernameError = validateUsername(form.username),
            passwordError = validatePassword(form.password),
            confirmPasswordError = validateConfirmPassword(form.password, form.confirmPassword),
            termsError = validateTerms(form.termsAndConditions),
            privacyPolicyError = validatePrivacy(form.privacyPolicy),
        )
    }

    private fun validateEmail(email: String): Option<StringResource> {
        return if (email.isBlank()) {
            Option.Some(Res.string.field_required)
        } else {
            Option.None
        }
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
        } else if (password.length < 4) {
            Option.Some(Res.string.password_too_short)
        } else {
            Option.None
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Option<StringResource> {
        return if (confirmPassword.isBlank()) {
            Option.Some(Res.string.field_required)
        } else if (password != confirmPassword) {
            Option.Some(Res.string.password_mismatch)
        } else {
            Option.None
        }
    }

    private fun validateTerms(accepted: Boolean): Option<StringResource> {
        return if (!accepted) {
            Option.Some(Res.string.terms_required)
        } else {
            Option.None
        }
    }

    private fun validatePrivacy(accepted: Boolean): Option<StringResource> {
        return if (!accepted) {
            Option.Some(Res.string.privacy_required)
        } else {
            Option.None
        }
    }
}

data class SignUpValidation(
    val emailError: Option<StringResource> = Option.None,
    val showEmailError: Boolean = false,
    val usernameError: Option<StringResource> = Option.None,
    val showUsernameError: Boolean = false,
    val passwordError: Option<StringResource> = Option.None,
    val showPasswordError: Boolean = false,
    val confirmPasswordError: Option<StringResource> = Option.None,
    val showConfirmPasswordError: Boolean = false,
    val termsError: Option<StringResource> = Option.None,
    val showTermsError: Boolean = false,
    val privacyPolicyError: Option<StringResource> = Option.None,
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
