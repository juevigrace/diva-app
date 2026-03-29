package com.diva.user.data

import com.diva.database.session.SessionStorage
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.auth.SignUpForm
import com.diva.models.user.User
import com.diva.user.api.client.UserApi
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import io.github.juevigrace.diva.core.pagination.Pagination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepository : Repository {
    fun getUsers(page: Int, pageSize: Int): Flow<DivaResult<Pagination<User>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun getUserById(id: Uuid): Flow<DivaResult<User, DivaError>>

    fun checkEmail(email: String): Flow<DivaResult<Boolean, DivaError>>

    fun checkUsername(username: String): Flow<DivaResult<Boolean, DivaError>>

    fun createUser(form: SignUpForm): Flow<DivaResult<String, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateUser(id: Uuid, user: User): Flow<DivaResult<Unit, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun deleteUser(id: Uuid): Flow<DivaResult<Unit, DivaError>>
}

class UserRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val userStorage: UserStorage,
    private val userClient: UserApi
) : UserRepository {
    override fun getUsers(
        page: Int,
        pageSize: Int,
    ): Flow<DivaResult<Pagination<User>, DivaError>> {
        return flow {
            userClient.getAll(page, pageSize)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res ->
                    scope.launch {
                        res.items.map { item ->
                            async { userStorage.insert(User.fromResponse(item)) }
                        }.awaitAll()
                    }
                    userStorage.getAll(page, pageSize)
                        .onFailure { err -> emit(DivaResult.failure(err)) }
                        .onSuccess { list ->
                            val pagination: Pagination<User> = Pagination(
                                items = list,
                                totalItems = res.pagination.totalItems,
                                totalPages = res.pagination.totalPages,
                                currentPage = res.pagination.page,
                                pageSize = res.pagination.limit
                            )
                            emit(DivaResult.success(pagination))
                        }
                }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getUserById(id: Uuid): Flow<DivaResult<User, DivaError>> {
        return flow {
            userStorage.getByIdFlow(id).collect { result ->
                result
                    .onFailure { err -> emit(DivaResult.failure(err)) }
                    .onSuccess { option ->
                        option.fold(
                            onNone = {
                                userClient.getById(id.toString())
                                    .onFailure { err -> emit(DivaResult.failure(err)) }
                                    .onSuccess { res ->
                                        userStorage.insert(User.fromResponse(res))
                                            .onFailure { err -> emit(DivaResult.failure(err)) }
                                    }
                            },
                            onSome = { user ->
                                emit(DivaResult.success(user))
                            }
                        )
                    }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun checkEmail(email: String): Flow<DivaResult<Boolean, DivaError>> {
        return flow {
            userClient.checkEmail(email)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res -> emit(DivaResult.success(res)) }
        }.flowOn(Dispatchers.IO)
    }

    override fun checkUsername(username: String): Flow<DivaResult<Boolean, DivaError>> {
        return flow {
            userClient.checkUsername(username)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res -> emit(DivaResult.success(res)) }
        }.flowOn(Dispatchers.IO)
    }

    override fun createUser(form: SignUpForm): Flow<DivaResult<String, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.create(form.toSignUpDto().user, value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res -> emit(DivaResult.success(res)) }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun updateUser(id: Uuid, user: User): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.update(
                id.toString(),
                UpdateUserDto(
                    alias = user.alias,
                    birthDate = user.birthDate.toEpochMilliseconds(),
                    bio = user.bio,
                    avatar = user.avatar
                ),
                value.accessToken
            )
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun deleteUser(id: Uuid): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.delete(id.toString(), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
