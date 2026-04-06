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
import io.github.juevigrace.diva.network.client.delete
import io.github.juevigrace.diva.network.client.get
import io.github.juevigrace.diva.network.client.patch
import io.github.juevigrace.diva.network.client.put
import io.ktor.client.call.body
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
            onError = { e -> e }
        ) {
            client.get(
                path = "/api/user/me",
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
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
                            val body: ApiResponse<Unit> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me"),
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

    override suspend fun updateMe(dto: UpdateUserDto, token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.put(
                path = "/api/user/me",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Accepted -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Unit> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me"),
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

    override suspend fun deleteMe(token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.delete(
                path = "/api/user/me",
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.NoContent -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Unit> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me"),
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

    override suspend fun updateEmail(
        dto: UpdateEmailDto,
        token: String,
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.patch(
                path = "/api/user/me/email",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Accepted -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Unit> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me/email"),
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
