package com.diva.user.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.pagination.response.PaginationResponse
import com.diva.models.api.user.dtos.CreateUserDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.api.user.response.UserResponse
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.errors.toDivaError
import io.github.juevigrace.diva.core.flatMap
import io.github.juevigrace.diva.core.network.HttpRequestMethod
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.delete
import io.github.juevigrace.diva.network.client.get
import io.github.juevigrace.diva.network.client.post
import io.github.juevigrace.diva.network.client.put
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface UserApi {
    suspend fun getAll(
        page: Int,
        pageSize: Int
    ): DivaResult<PaginationResponse<UserResponse>, DivaError>
    suspend fun getById(id: String): DivaResult<UserResponse, DivaError>
    suspend fun checkEmail(email: String): DivaResult<Boolean, DivaError>
    suspend fun checkUsername(username: String): DivaResult<Boolean, DivaError>
    suspend fun create(dto: CreateUserDto, token: String): DivaResult<String, DivaError>
    suspend fun update(id: String, dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError>
    suspend fun delete(id: String, token: String): DivaResult<Unit, DivaError>
}

class UserApiImpl(
    private val client: DivaClient
) : UserApi {
    override suspend fun getAll(
        page: Int,
        pageSize: Int,
    ): DivaResult<PaginationResponse<UserResponse>, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(path = "/api/user").flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<PaginationResponse<UserResponse>> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun getById(id: String): DivaResult<UserResponse, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(path = "/api/user/$id").flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<UserResponse> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user/{id}",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun checkEmail(email: String): DivaResult<Boolean, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(
                path = "/api/user/check/email/$email",
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        DivaResult.success(true)
                    }
                    HttpStatusCode.Conflict -> {
                        DivaResult.success(false)
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user/{id}",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun checkUsername(username: String): DivaResult<Boolean, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(
                path = "/api/user/check/username/$username",
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        DivaResult.success(true)
                    }
                    HttpStatusCode.Conflict -> {
                        DivaResult.success(false)
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user/check/{username}",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun create(
        dto: CreateUserDto,
        token: String
    ): DivaResult<String, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.Created -> {
                        val body: ApiResponse<String> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/user",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun update(
        id: String,
        dto: UpdateUserDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.put(
                path = "/api/user/$id",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.Accepted -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PUT,
                                    url = "/api/user/{id}",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun delete(id: String, token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.delete(
                path = "/api/user/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.NoContent -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/user/{id}",
                                    status = response.status.toHttpStatusCodes(),
                                    details = Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
