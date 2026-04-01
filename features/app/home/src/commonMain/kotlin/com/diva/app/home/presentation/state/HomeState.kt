package com.diva.app.home.presentation.state

import com.diva.models.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeState
@OptIn(ExperimentalUuidApi::class)
constructor(
    val user: User = User(id = Uuid.NIL)
)
