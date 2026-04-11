package com.diva.verification.data.validation

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.field_min_length
import com.diva.core.ui.resources.field_numbers_only
import com.diva.core.ui.resources.field_required
import com.diva.core.ui.resources.token
import com.diva.models.verification.VerificationForm
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.validation.ValidationResult
import io.github.juevigrace.diva.core.validation.Validator
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

object VerificationValidator : Validator<VerificationForm, VerificationValidation> {
    const val TOKEN_LENGTH = 6

    override fun validate(form: VerificationForm): VerificationValidation {
        return VerificationValidation(
            tokenError = validateToken(form.token),
        )
    }

    private fun validateToken(token: String): Option<String> {
        val fieldName = Res.string.token
        return when {
            token.isBlank() -> {
                val value = runBlocking {
                    getString(Res.string.field_required, getString(fieldName))
                }
                Option.Some(value)
            }
            !token.any { it.isDigit() } -> {
                val value = runBlocking {
                    getString(Res.string.field_numbers_only, getString(fieldName))
                }
                Option.Some(value)
            }
            token.length != TOKEN_LENGTH -> {
                val value = runBlocking {
                    getString(Res.string.field_min_length, getString(fieldName), TOKEN_LENGTH)
                }
                Option.Some(value)
            }
            else -> {
                Option.None
            }
        }
    }
}

data class VerificationValidation(
    val tokenError: Option<String> = Option.None,
    val showTokenError: Boolean = false,
) : ValidationResult {
    override val hasErrors: Boolean = showTokenError

    override fun valid(): Boolean {
        return hasErrors && tokenError is Option.None
    }
}
