package com.diva.user.data

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.auth.SignUpForm
import com.diva.models.user.User
import com.diva.user.api.client.UserApi
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.pagination.Pagination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepository : Repository {
    fun getUsers(page: Int, pageSize: Int): Flow<Result<Pagination<User>>>

    @OptIn(ExperimentalUuidApi::class)
    fun getUserById(id: Uuid): Flow<Result<User>>

    suspend fun checkEmail(email: String): Result<Boolean>

    suspend fun checkUsername(username: String): Result<Boolean>

    suspend fun createUser(form: SignUpForm): Result<String>

    suspend fun updateUser(user: User): Result<Unit>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteUser(id: Uuid): Result<Unit>
}

class UserRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val userStorage: UserStorage,
    private val userClient: UserApi,
) : UserRepository {
    override fun getUsers(
        page: Int,
        pageSize: Int,
    ): Flow<Result<Pagination<User>>> {
        return flow {
            userClient.getAll(page, pageSize).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res ->
                    res.items.forEach { item ->
                        userStorage.upsert(User.fromResponse(item))
                    }
                    userStorage.getAll(page, pageSize).fold(
                        onFailure = { err -> emit(Result.failure(err)) },
                        onSuccess = { list ->
                            val pagination: Pagination<User> = Pagination(
                                items = list,
                                totalItems = res.pagination.totalItems,
                                totalPages = res.pagination.totalPages,
                                currentPage = res.pagination.page,
                                pageSize = res.pagination.limit
                            )
                            emit(Result.success(pagination))
                        }
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getUserById(id: Uuid): Flow<Result<User>> {
        return flow {
            userStorage.getByIdFlow(id).collect { result ->
                result.fold(
                    onFailure = { err -> emit(Result.failure(err)) },
                    onSuccess = { option ->
                        option.fold(
                            onNone = {
                                userClient.getById(id.toString()).fold(
                                    onFailure = { err -> emit(Result.failure(err)) },
                                    onSuccess = { res ->
                                        userStorage.upsert(User.fromResponse(res)).onFailure { err ->
                                            emit(Result.failure(err))
                                        }
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
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun checkEmail(email: String): Result<Boolean> {
        return userClient.checkEmail(email)
    }

    override suspend fun checkUsername(username: String): Result<Boolean> {
        return userClient.checkUsername(username)
    }

    override suspend fun createUser(form: SignUpForm): Result<String> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.create(form.toSignUpDto().user, value.accessToken)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateUser(user: User): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.update(
                user.id.toString(),
                UpdateUserDto(
                    alias = user.alias,
                    birthDate = user.birthDate.toEpochMilliseconds(),
                    bio = user.bio,
                    avatar = user.avatar
                ),
                value.accessToken
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteUser(id: Uuid): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.delete(id.toString(), value.accessToken).onFailure { err ->
                return@withSession Result.failure(err)
            }
            userStorage.delete(id)
        }
    }
}
