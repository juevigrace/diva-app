package com.diva.user.data

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.auth.SignUpForm
import com.diva.models.user.User
import com.diva.user.api.client.UserApi
import com.diva.user.api.client.me.UserMeApi
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

    fun checkEmail(email: String): Flow<Result<Boolean>>

    fun checkUsername(username: String): Flow<Result<Boolean>>

    fun createUser(form: SignUpForm): Flow<Result<String>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateUser(id: Uuid, user: User): Flow<Result<Unit>>

    @OptIn(ExperimentalUuidApi::class)
    fun deleteUser(id: Uuid): Flow<Result<Unit>>
}

class UserRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val userStorage: UserStorage,
    private val userClient: UserApi,
    private val userMeClient: UserMeApi,
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
                        userStorage.insert(User.fromResponse(item))
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
                                        userStorage.insert(User.fromResponse(res)).fold(
                                            onFailure = { err -> emit(Result.failure(err)) },
                                            onSuccess = { }
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
        }.flowOn(Dispatchers.IO)
    }

    override fun checkEmail(email: String): Flow<Result<Boolean>> {
        return flow {
            userClient.checkEmail(email).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res -> emit(Result.success(res)) }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun checkUsername(username: String): Flow<Result<Boolean>> {
        return flow {
            userClient.checkUsername(username).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res -> emit(Result.success(res)) }
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun createUser(form: SignUpForm): Flow<Result<String>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.create(form.toSignUpDto().user, value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { res -> emit(Result.success(res)) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun updateUser(id: Uuid, user: User): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.update(
                id.toString(),
                UpdateUserDto(
                    alias = user.alias,
                    birthDate = user.birthDate.toEpochMilliseconds(),
                    bio = user.bio,
                    avatar = user.avatar
                ),
                value.accessToken
            ).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun deleteUser(id: Uuid): Flow<Result<Unit>> {
        return withSession(sessionRepository::getCurrent) { value ->
            userClient.delete(id.toString(), value.accessToken).fold(
                onFailure = { err -> emit(Result.failure(err)) },
                onSuccess = { emit(Result.success(Unit)) }
            )
        }
    }
}
