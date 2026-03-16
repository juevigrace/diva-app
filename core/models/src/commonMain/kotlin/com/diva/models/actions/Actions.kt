package com.diva.models.actions

import com.diva.models.api.action.ActionResponse
import io.github.juevigrace.diva.core.errors.DivaAction


enum class Actions {
    EMAIL_VERIFICATION,
    UNKNOWN,
}

sealed class AppActions(
    override val key: String,
    override val required: Boolean
) : DivaAction {
    object EmailVerification : AppActions(
        Actions.EMAIL_VERIFICATION.name,
        true
    )

    object Unknown : AppActions(
        Actions.UNKNOWN.name,
        false
    )

    companion object {
        fun fromAction(action: Actions): AppActions {
            return when (action) {
                Actions.EMAIL_VERIFICATION -> EmailVerification
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
