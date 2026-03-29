package com.diva.verification.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.field_required
import com.diva.models.verification.VerificationForm
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import org.jetbrains.compose.resources.StringResource

object VerificationValidator : Validator<VerificationForm, VerificationValidation> {
    override fun validate(form: VerificationForm): VerificationValidation {
        return VerificationValidation(
            tokenError = validateToken(form.token),
        )
    }

    private fun validateToken(token: String): Option<StringResource> {
        return when {
            token.isBlank() -> Option.Some(Res.string.field_required)
            token.length != 6 -> Option.Some(Res.string.field_required)
            !token.all { it.isDigit() } -> Option.Some(Res.string.field_required)
            else -> Option.None
        }
    }
}

data class VerificationValidation(
    val tokenError: Option<StringResource> = Option.None,
    val showTokenError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showTokenError

    override fun valid(): Boolean {
        return hasErrors && tokenError is Option.None
    }
}
