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
import io.github.juevigrace.diva.network.client.toDivaNetworkException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
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
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(path = "/api/user").getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body: ApiResponse<PaginationResponse<UserResponse>> = response.body()
                    body.data ?: throw ConstraintException(
                        field = "data",
                        constraint = "missing",
                        value = body.message
                    )
                }
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun getById(id: String): Result<UserResponse> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(path = "/api/user/$id").getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body: ApiResponse<UserResponse> = response.body()
                    body.data ?: throw ConstraintException(
                        field = "data",
                        constraint = "missing",
                        value = body.message
                    )
                }
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/{id}"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun checkEmail(email: String): Result<Boolean> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(
                path = "/api/user/check/email/$email",
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> true
                HttpStatusCode.Conflict -> false
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/check/email/$email"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun checkUsername(username: String): Result<Boolean> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(
                path = "/api/user/check/username/$username",
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> true
                HttpStatusCode.Conflict -> false
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/check/username/$username"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun create(
        dto: CreateUserDto,
        token: String
    ): Result<String> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/user",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = CreateUserDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Created -> {
                    val body: ApiResponse<String> = response.body()
                    body.data ?: throw ConstraintException(
                        field = "data",
                        constraint = "missing",
                        value = body.message
                    )
                }
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun update(
        id: String,
        dto: UpdateUserDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.put(
                path = "/api/user/$id",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UpdateUserDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Accepted -> return@tryResult
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/$id"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun delete(id: String, token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.delete(
                path = "/api/user/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.NoContent -> return@tryResult
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/$id"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }
}
