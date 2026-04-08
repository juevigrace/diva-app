package com.diva.user.api.client.me

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.dtos.UpdateEmailDto
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

interface UserMeApi {
    suspend fun getMe(token: String): Result<UserResponse>
    suspend fun updateMe(dto: UpdateUserDto, token: String): Result<Unit>
    suspend fun deleteMe(token: String): Result<Unit>
    suspend fun updateEmail(
        dto: UpdateEmailDto,
        token: String
    ): Result<Unit>
}

class UserMeApiImpl(
    private val client: DivaClient
) : UserMeApi {
    override suspend fun getMe(token: String): Result<UserResponse> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(
                path = "/api/user/me",
                headers = mapOf("Authorization" to "Bearer $token")
            ).getOrThrow()
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
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun updateMe(dto: UpdateUserDto, token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.put(
                path = "/api/user/me",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UpdateUserDto.serializer()
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Accepted -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun deleteMe(token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.delete(
                path = "/api/user/me",
                headers = mapOf("Authorization" to "Bearer $token")
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.NoContent -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun updateEmail(
        dto: UpdateEmailDto,
        token: String,
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.patch(
                path = "/api/user/me/email",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UpdateEmailDto.serializer()
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Accepted -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me/email"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }
}
