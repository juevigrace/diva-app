package com.diva.app.home.data

import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.user.User
import com.diva.models.user.actions.UserAction
import com.diva.user.data.actions.UserActionsRepository
import com.diva.user.data.me.UserMeRepository
import kotlinx.coroutines.flow.Flow

interface HomeRepository : Repository {
    fun getMe(): Flow<Result<User>>
    fun getActions(): Flow<Result<Map<Actions, UserAction>>>
}

class HomeRepositoryImpl(
    private val umeRepository: UserMeRepository,
    private val uaRepository: UserActionsRepository,
) : HomeRepository {
    override fun getActions(): Flow<Result<Map<Actions, UserAction>>> {
        return uaRepository.getActions()
    }

    override fun getMe(): Flow<Result<User>> {
        return umeRepository.getMe()
    }
}
