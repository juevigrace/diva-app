package com.diva.models.user.actions

import com.diva.models.actions.Actions
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class UserAction(
    val id: Uuid,
    val action: Actions,
)
