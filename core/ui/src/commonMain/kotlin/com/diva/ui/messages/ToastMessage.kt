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
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.ui.toast.ToastMessage
import org.jetbrains.compose.resources.getString

suspend inline fun DivaError.toToast(): ToastMessage {
    return ToastMessage(
        message = when (cause) {
            is ErrorCause.Database.Duplicated -> getString(Res.string.error_database_duplicated)
            is ErrorCause.Database.NoRowsAffected -> getString(Res.string.error_database_no_rows_affected)
            is ErrorCause.Error.Ex -> getString(Res.string.error_unknown)
            is ErrorCause.Error.NotImplemented -> getString(Res.string.error_not_implemented)
            is ErrorCause.Network.Error -> getString(Res.string.error_network)
            is ErrorCause.Network.NoConnection -> getString(Res.string.error_no_connection)
            is ErrorCause.Network.Timeout -> getString(Res.string.error_timeout)
            is ErrorCause.Validation.Expired -> getString(Res.string.error_validation_expired)
            is ErrorCause.Validation.MissingValue -> getString(Res.string.error_validation_missing_value)
            is ErrorCause.Validation.Parse -> getString(Res.string.error_validation_parse)
            is ErrorCause.Validation.UnexpectedValue -> getString(Res.string.error_validation_unexpected_value)
            is ErrorCause.Validation.Used -> getString(Res.string.error_validation_used)
            is ErrorCause.Actions<*> -> ""
        },
        isError = true,
        details = Option.of(message)
    )
}
