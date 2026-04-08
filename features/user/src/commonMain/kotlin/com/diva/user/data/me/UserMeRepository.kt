package com.diva.user.data.me

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateEmailDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.user.User
import com.diva.user.api.client.me.UserMeApi
import io.github.juevigrace.diva.core.errors.DuplicateKeyException
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.flow.Flow
import kotlin.fold
import kotlin.uuid.ExperimentalUuidApi

interface UserMeRepository : Repository {
    fun getMe(): Flow<Result<User>>
    fun updateMe(user: User): Flow<Result<Unit>>
    fun deleteMe(): Flow<Result<Unit>>
    fun updateEmail(email: String): Flow<Result<Unit>>
}

class UserMeRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val userMeClient: UserMeApi,
    private val storage: UserStorage,
) : UserMeRepository {

    @OptIn(ExperimentalUuidApi::class)
    override fun getMe(): Flow<Result<User>> {
        return withSession(sessionRepository::getCurrent) { session ->
            storage.getById(session.user.id).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { option ->
                    option.fold(
                        onNone = {
                            userMeClient.getMe(session.accessToken).fold(
                                onFailure = { err -> emit(Result.failure(err)) },
                                onSuccess = { res ->
                                    val user = User.fromResponse(res)
                                    upsertUser(user).fold(
                                        onFailure = { err -> emit(Result.failure(err)) },
                                        onSuccess = {
                                            getMe().collect { emit(it) }
                                        }
                                    )
                                }
                            )
                        },
                        onSome = { user ->
                            emit(Result.success(user))
                        }
                    )
                }
            )
        }
    }

    private suspend fun upsertUser(user: User): Result<Unit> {
        return storage.insert(user).fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { err ->
                if (err is DuplicateKeyException) {
                    storage.update(user)
                } else {
                    Result.failure(err)
                }
            }
        )
    }

    override fun updateMe(user: User): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            val dto = UpdateUserDto(
                alias = user.alias,
                birthDate = user.birthDate.toEpochMilliseconds(),
                bio = user.bio,
                avatar = user.avatar
            )
            userMeClient.updateMe(dto, value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = {
                    storage.update(user).fold(
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = { emit(Result.success(Unit)) }
                    )
                }
            )
        }
    }

    override fun deleteMe(): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.deleteMe(value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    override fun updateEmail(email: String): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.updateEmail(UpdateEmailDto(email), value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }
}
