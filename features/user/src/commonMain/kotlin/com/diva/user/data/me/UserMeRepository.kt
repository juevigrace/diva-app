package com.diva.user.data.me

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateEmailDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.user.User
import com.diva.user.api.client.me.UserMeApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.flatMapError
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.map
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi

interface UserMeRepository : Repository {
    fun getMe(): Flow<DivaResult<User, DivaError>>
    fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>>
    fun deleteMe(): Flow<DivaResult<Unit, DivaError>>
    fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>>
}

class UserMeRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val userMeClient: UserMeApi,
    private val storage: UserStorage,
) : UserMeRepository {

    @OptIn(ExperimentalUuidApi::class)
    override fun getMe(): Flow<DivaResult<User, DivaError>> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.getById(session.user.id).fold(
                onFailure = { err -> emit(DivaResult.failure(err)) },
                onSuccess = { option ->
                    option.fold(
                        onNone = {
                            fetchMe(session.accessToken).onFailure { err ->
                                emit(DivaResult.failure(err))
                            }
                        },
                        onSome = { user ->
                            emit(DivaResult.success(user))
                        }
                    )
                }
            )
        }
    }

    private suspend fun fetchMe(token: String): DivaResult<Unit, DivaError> {
        return userMeClient.getMe(token).map { res ->
            val user = User.fromResponse(res)
            upsertUser(user)
        }
    }

    private suspend fun upsertUser(user: User): DivaResult<Unit, DivaError> {
        return storage.insert(user)
            .flatMapError { err ->
                val cause = err.cause
                return@flatMapError if (
                    cause is ErrorCause.Error.Ex &&
                    cause.ex.message?.contains("SQLITE_CONSTRAINT_UNIQUE") == true
                ) {
                    storage.update(user)
                } else {
                    DivaResult.failure(err)
                }
            }
    }

    override fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionRepository::getCurrent) { value ->
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
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.deleteMe(value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.updateEmail(UpdateEmailDto(email), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
