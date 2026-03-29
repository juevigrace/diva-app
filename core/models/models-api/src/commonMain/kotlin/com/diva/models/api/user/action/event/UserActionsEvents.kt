package com.diva.models.api.user.action.event

import com.diva.models.api.user.action.response.ActionResponse

sealed interface UserActionsEvents {
    data class Actions(val list: List<ActionResponse>) : UserActionsEvents
    data object End : UserActionsEvents
}
