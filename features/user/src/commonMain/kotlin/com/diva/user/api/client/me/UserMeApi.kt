package com.diva.user.api.client.me

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.dtos.UpdateEmailDto
import com.diva.models.api.user.dtos.UpdateUserDto
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
import io.github.juevigrace.diva.network.client.patch
import io.github.juevigrace.diva.network.client.put
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface UserMeApi {
    suspend fun updateMe(dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError>
    suspend fun deleteMe(token: String): DivaResult<Unit, DivaError>
    suspend fun updateEmail(
        dto: UpdateEmailDto,
        token: String
    ): DivaResult<Unit, DivaError>
}

class UserMeApiImpl(
    private val client: DivaClient
) : UserMeApi {
    override suspend fun updateMe(dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.put(
                path = "/api/user/me",
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
                                    url = "/api/user/me",
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

    override suspend fun deleteMe(token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.delete(
                path = "/api/user/me",
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
                                    url = "/api/user/me",
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

    override suspend fun updateEmail(
        dto: UpdateEmailDto,
        token: String,
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.patch(
                path = "/api/user/me/email",
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
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/user/me/email",
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
