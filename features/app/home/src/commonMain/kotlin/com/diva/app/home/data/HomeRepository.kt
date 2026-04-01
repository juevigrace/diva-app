package com.diva.app.home.data

import com.diva.models.actions.Actions
import com.diva.models.user.actions.UserAction
import com.diva.user.data.actions.UserActionsRepository
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>>
}

class HomeRepositoryImpl(
    private val uaRepository: UserActionsRepository
) : HomeRepository {
    override fun getActions(): Flow<DivaResult<Map<Actions, UserAction>, DivaError>> {
        return uaRepository.getActions()
    }
}
