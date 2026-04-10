package com.diva.user.data.me

import com.diva.auth.session.data.SessionRepository
import com.diva.database.user.UserStorage
import com.diva.models.Repository
import com.diva.models.api.user.dtos.UpdateEmailDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.user.User
import com.diva.user.api.client.me.UserMeApi
import io.github.juevigrace.diva.core.fold
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi

interface UserMeRepository : Repository {
    suspend fun getMe(): Flow<Result<User>>
    suspend fun updateMe(user: User): Result<Unit>
    suspend fun deleteMe(): Result<Unit>
    suspend fun updateEmail(email: String): Result<Unit>
}

class UserMeRepositoryImpl(
    private val sessionRepository: SessionRepository,
    private val userMeClient: UserMeApi,
    private val storage: UserStorage,
) : UserMeRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getMe(): Flow<Result<User>> {
        return withSessionFlow(sessionRepository::getCurrent) { session ->
            val fetch = fetchUser(session.accessToken)
            storage.getByIdFlow(session.user.id).collect { res ->
                res.fold(
                    onFailure = { err -> emit(Result.failure(err)) },
                    onSuccess = { option ->
                        option.fold(
                            onNone = {
                                fetch.onFailure { err -> emit(Result.failure(err)) }
                            },
                            onSome = { user -> emit(Result.success(user)) }
                        )
                    }
                )
                fetch.onFailure { err -> emit(Result.failure(err)) }
            }
        }
    }

    private suspend fun fetchUser(token: String): Result<Unit> {
        return userMeClient.getMe(token).fold(
            onFailure = { err -> Result.failure(err) },
            onSuccess = { res -> storage.upsert(User.fromResponse(res)) }
        )
    }

    override suspend fun updateMe(user: User): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { value ->
            val dto = UpdateUserDto(
                alias = user.alias,
                birthDate = user.birthDate.toEpochMilliseconds(),
                bio = user.bio,
                avatar = user.avatar
            )
            userMeClient.updateMe(dto, value.accessToken)
        }
    }

    // TODO: this might fail as it is, refactor
    override suspend fun deleteMe(): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.deleteMe(value.accessToken).fold(
                onFailure = { err -> Result.failure(err) },
                onSuccess = { sessionRepository.logout() }
            )
        }
    }

    override suspend fun updateEmail(email: String): Result<Unit> {
        return withSession(sessionRepository::getCurrent) { value ->
            userMeClient.updateEmail(UpdateEmailDto(email), value.accessToken)
        }
    }
}
