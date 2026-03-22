package com.diva.models.actions

import com.diva.models.api.action.response.ActionResponse
import io.github.juevigrace.diva.core.errors.DivaAction

enum class Actions {
    USER_VERIFICATION,
    PASSWORD_VERIFICATION,
    UNKNOWN,
}

sealed class AppActions(
    override val key: String,
    override val required: Boolean
) : DivaAction {
    object UserVerification : AppActions(
        Actions.USER_VERIFICATION.name,
        true
    )

    object PasswordVerification : AppActions(
        Actions.PASSWORD_VERIFICATION.name,
        true
    )

    object Unknown : AppActions(
        Actions.UNKNOWN.name,
        false
    )

    companion object {
        fun fromAction(action: Actions): AppActions {
            return when (action) {
                Actions.USER_VERIFICATION -> UserVerification
                Actions.PASSWORD_VERIFICATION -> PasswordVerification
                Actions.UNKNOWN -> Unknown
            }
        }
    }
}

fun ActionResponse.toAction(): Actions {
    return try {
        Actions.valueOf(actionName)
    } catch (e: IllegalArgumentException) {
        Actions.UNKNOWN
    }
}
