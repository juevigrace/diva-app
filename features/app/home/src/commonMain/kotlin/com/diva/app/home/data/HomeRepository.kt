package com.diva.app.home.data

import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.user.actions.UserAction
import com.diva.user.data.actions.UserActionsRepository
import kotlinx.coroutines.flow.Flow

interface HomeRepository : Repository {
    fun getActions(): Flow<Result<Map<Actions, UserAction>>>
}

class HomeRepositoryImpl(
    private val uaRepository: UserActionsRepository
) : HomeRepository {
    override fun getActions(): Flow<Result<Map<Actions, UserAction>>> {
        return uaRepository.getActions()
    }
}
