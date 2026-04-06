package com.diva.ui.messages

import com.diva.core.ui.resources.Res
import com.diva.core.ui.resources.error_database_duplicated
import com.diva.core.ui.resources.error_database_no_rows_affected
import com.diva.core.ui.resources.error_network
import com.diva.core.ui.resources.error_no_connection
import com.diva.core.ui.resources.error_not_implemented
import com.diva.core.ui.resources.error_timeout
import com.diva.core.ui.resources.error_unknown
import com.diva.core.ui.resources.error_validation_expired
import com.diva.core.ui.resources.error_validation_missing_value
import com.diva.core.ui.resources.error_validation_parse
import com.diva.core.ui.resources.error_validation_unexpected_value
import com.diva.core.ui.resources.error_validation_used
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.ConstraintViolationException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.errors.NetworkConnectionException
import io.github.juevigrace.diva.core.errors.NetworkTimeoutException
import io.github.juevigrace.diva.core.errors.NoRowsAffectedException
import io.github.juevigrace.diva.core.errors.ProcessingException
import io.github.juevigrace.diva.ui.toast.ToastMessage
import org.jetbrains.compose.resources.getString

suspend inline fun Throwable.toToast(): ToastMessage {
    return ToastMessage(
        message = when (this) {
            is ConstraintViolationException -> getString(Res.string.error_database_duplicated)
            is NoRowsAffectedException -> getString(Res.string.error_database_no_rows_affected)
            is ProcessingException -> getString(Res.string.error_unknown)
            is HttpException -> getString(Res.string.error_network)
            is NetworkConnectionException -> getString(Res.string.error_no_connection)
            is NetworkTimeoutException -> getString(Res.string.error_timeout)
            is ConstraintException -> when (constraint) {
                "expired" -> getString(Res.string.error_validation_expired)
                "missing" -> getString(Res.string.error_validation_missing_value)
                "parse" -> getString(Res.string.error_validation_parse)
                "unexpected" -> getString(Res.string.error_validation_unexpected_value)
                "used" -> getString(Res.string.error_validation_used)
                else -> getString(Res.string.error_unknown)
            }
            else -> getString(Res.string.error_unknown)
        },
        isError = true,
        details = Option.of(message)
    )
}
