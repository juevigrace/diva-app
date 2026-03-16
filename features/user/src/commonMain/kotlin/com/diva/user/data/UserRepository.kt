package com.diva.user.data

import com.diva.database.session.SessionStorage
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.auth.dtos.PasswordUpdateDto
import com.diva.models.api.user.dtos.EmailTokenDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.api.user.dtos.UserEmailDto
import com.diva.models.auth.Session
import com.diva.models.auth.SignUpForm
import com.diva.models.preferences.PreferenceType
import com.diva.models.user.User
import com.diva.models.user.preferences.UserPreferences
import com.diva.user.api.client.UserNetworkClient
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.fold
import io.github.juevigrace.diva.core.getOrThrow
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

// TODO: break down
interface UserRepository : Repository {
    fun getUsers(page: Int, pageSize: Int): Flow<DivaResult<Pagination<User>, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun getUserById(id: Uuid): Flow<DivaResult<User, DivaError>>

    fun createUser(form: SignUpForm): Flow<DivaResult<String, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateUser(id: Uuid, user: User): Flow<DivaResult<Unit, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun deleteUser(id: Uuid): Flow<DivaResult<Unit, DivaError>>

    fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>>

    fun deleteMe(): Flow<DivaResult<Unit, DivaError>>

    fun requestEmailUpdate(email: String): Flow<DivaResult<Unit, DivaError>>

    fun confirmEmailUpdate(token: String): Flow<DivaResult<Unit, DivaError>>

    fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>>

    fun getLocalPreferences(): Flow<DivaResult<UserPreferences, DivaError>>

    fun createPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>>

    fun updatePreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>>

    @OptIn(ExperimentalUuidApi::class)
    fun updateLocalPrefUserId(id: Uuid): Flow<DivaResult<Unit, DivaError>>

    fun syncPreferences(): Flow<DivaResult<Unit, DivaError>>
}

// TODO: make local database logic
class UserRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val userStorage: UserStorage,
    private val userClient: UserNetworkClient
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
                                totalItems = res.totalItems,
                                totalPages = res.totalPages,
                                currentPage = res.currentPage,
                                pageSize = res.pageSize
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
                    username = user.username,
                    alias = user.alias,
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

    override fun updateMe(user: User): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.updateMe(
                UpdateUserDto(
                    username = user.username,
                    alias = user.alias,
                    bio = user.bio,
                    avatar = user.avatar
                ),
                value.accessToken
            )
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun deleteMe(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.deleteMe(value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun requestEmailUpdate(email: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.requestEmailUpdate(UserEmailDto(email), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun confirmEmailUpdate(token: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.confirmEmailUpdate(EmailTokenDto(token), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun updateEmail(email: String): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.updateEmail(UserEmailDto(email), value.accessToken)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun getLocalPreferences(): Flow<DivaResult<UserPreferences, DivaError>> {
        return flow {
            userStorage.findLocalPreferences()
                .onFailure { emit(DivaResult.failure(it)) }
                .onSuccess { opt ->
                    opt.fold(
                        onNone = {
                            emit(DivaResult.failure(DivaError(ErrorCause.Validation.MissingValue("preferences"))))
                        },
                        onSome = { prefs ->
                            emit(DivaResult.success(prefs))
                        }
                    )
                }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun createPreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            val res = when (prefs.type) {
                PreferenceType.LOCAL -> userStorage.insertLocalPreferences(prefs)
                PreferenceType.CLOUD -> {
                    val session: Option<Session> = sessionStorage.getCurrentSession().getOrThrow()
                    session.fold(
                        onNone = {
                            return@flow emit(
                                DivaResult.failure(
                                    DivaError(
                                        ErrorCause.Validation.MissingValue("session")
                                    )
                                )
                            )
                        },
                        onSome = { value ->
                            userStorage.insertCloudPreferences(value.user.id, prefs)
                        }
                    )
                }
            }
            res
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess {
                    emit(DivaResult.success(Unit))
                }
        }.flowOn(Dispatchers.IO)
    }

    override fun updatePreferences(prefs: UserPreferences): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            userStorage.updatePreferences(prefs)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }.flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun updateLocalPrefUserId(id: Uuid): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userStorage.updatePreferenceUserId(id, value.user.id)
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }

    override fun syncPreferences(): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { value ->
            userClient.updatePreferences(value.user.preferences.toDto(), value.accessToken)
                .onFailure { err ->
                    // TODO: check in api which error is returned, if not able to update because
                    // preferences are not found then create them
                    if (err.cause is ErrorCause.Validation.MissingValue) {
                        userClient.createPreferences(value.user.preferences.toDto(), value.accessToken)
                            .onFailure { err -> emit(DivaResult.failure(err)) }
                            .onSuccess { emit(DivaResult.success(Unit)) }
                    }
                }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
