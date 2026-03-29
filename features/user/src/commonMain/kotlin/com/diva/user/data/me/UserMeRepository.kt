package com.diva.user.data.me

import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateEmailDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.user.User
import com.diva.user.api.client.me.UserMeApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.flow.Flow

interface UserMeRepository : Repository {
    fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>>
    fun deleteMe(): Flow<DivaResult<Unit, DivaError>>
    fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>>
}

class UserMeRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val userMeClient: UserMeApi
) : UserMeRepository {
    override fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            val dto = UpdateUserDto(
                alias = user.alias,
                birthDate = user.birthDate.toEpochMilliseconds(),
                bio = user.bio,
                avatar = user.avatar
            )
            userMeClient.updateMe(dto, value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun deleteMe(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userMeClient.deleteMe(value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userMeClient.updateEmail(UpdateEmailDto(email), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
