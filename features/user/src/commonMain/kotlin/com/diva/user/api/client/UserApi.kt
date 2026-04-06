package com.diva.user.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.pagination.response.PaginationResponse
import com.diva.models.api.user.dtos.CreateUserDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.api.user.response.UserResponse
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.delete
import io.github.juevigrace.diva.network.client.get
import io.github.juevigrace.diva.network.client.post
import io.github.juevigrace.diva.network.client.put
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface UserApi {
    suspend fun getAll(
        page: Int,
        pageSize: Int
    ): Result<PaginationResponse<UserResponse>>
    suspend fun getById(id: String): Result<UserResponse>
    suspend fun checkEmail(email: String): Result<Boolean>
    suspend fun checkUsername(username: String): Result<Boolean>
    suspend fun create(dto: CreateUserDto, token: String): Result<String>
    suspend fun update(id: String, dto: UpdateUserDto, token: String): Result<Unit>
    suspend fun delete(id: String, token: String): Result<Unit>
}

class UserApiImpl(
    private val client: DivaClient
) : UserApi {
    override suspend fun getAll(
        page: Int,
        pageSize: Int,
    ): Result<PaginationResponse<UserResponse>> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.get(path = "/api/user").fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> {
                            val body: ApiResponse<PaginationResponse<UserResponse>> = response.body()
                            body.data?.let { data -> Result.success(data) }
                                ?: Result.failure(
                                    ConstraintException(
                                        field = "data",
                                        constraint = "missing",
                                        value = body.message
                                    )
                                )
                        }
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun getById(id: String): Result<UserResponse> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.get(path = "/api/user/$id").fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> {
                            val body: ApiResponse<UserResponse> = response.body()
                            body.data?.let { data -> Result.success(data) }
                                ?: Result.failure(
                                    ConstraintException(
                                        field = "data",
                                        constraint = "missing",
                                        value = body.message
                                    )
                                )
                        }
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/{id}"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun checkEmail(email: String): Result<Boolean> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.get(
                path = "/api/user/check/email/$email",
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> {
                            Result.success(true)
                        }
                        HttpStatusCode.Conflict -> {
                            Result.success(false)
                        }
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/check/email/$email"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun checkUsername(username: String): Result<Boolean> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.get(
                path = "/api/user/check/username/$username",
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> {
                            Result.success(true)
                        }
                        HttpStatusCode.Conflict -> {
                            Result.success(false)
                        }
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/check/username/$username"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun create(
        dto: CreateUserDto,
        token: String
    ): Result<String> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.post(
                path = "/api/user",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Created -> {
                            val body: ApiResponse<String> = response.body()
                            body.data?.let { data -> Result.success(data) }
                                ?: Result.failure(
                                    ConstraintException(
                                        field = "data",
                                        constraint = "missing",
                                        value = body.message
                                    )
                                )
                        }
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun update(
        id: String,
        dto: UpdateUserDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.put(
                path = "/api/user/$id",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Accepted -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/$id"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }

    override suspend fun delete(id: String, token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.delete(
                path = "/api/user/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.NoContent -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/$id"),
                                    details = Option.of(body.message)
                                )
                            )
                        }
                    }
                },
                onFailure = { Result.failure(it) }
            )
        }
    }
}
