package com.diva.auth.data.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.auth.dtos.SessionDataDto
import com.diva.models.api.auth.dtos.SignInDto
import com.diva.models.api.auth.dtos.SignUpDto
import com.diva.models.api.auth.response.AuthResponse
import com.diva.models.api.auth.response.SessionResponse
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.errors.toDivaError
import io.github.juevigrace.diva.core.flatMap
import io.github.juevigrace.diva.core.network.HttpRequestMethod
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.post
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface AuthNetworkClient {
    suspend fun signIn(dto: SignInDto): DivaResult<AuthResponse, DivaError>
    suspend fun signUp(dto: SignUpDto): DivaResult<AuthResponse, DivaError>
    suspend fun signOut(token: String): DivaResult<Unit, DivaError>
    suspend fun ping(token: String): DivaResult<Unit, DivaError>
    suspend fun refresh(dto: SessionDataDto, token: String): DivaResult<SessionResponse, DivaError>
}

class AuthNetworkClientImpl(
    private val client: DivaClient
) : AuthNetworkClient {
    override suspend fun signIn(dto: SignInDto): DivaResult<AuthResponse, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/auth/signIn",
                body = dto
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<AuthResponse> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue(
                                        field = "data",
                                        details = Option.Some(body.message)
                                    )
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/auth/signIn",
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

    override suspend fun signUp(dto: SignUpDto): DivaResult<AuthResponse, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/auth/signUp",
                body = dto
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.Created -> {
                        val body: ApiResponse<AuthResponse> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue(
                                        field = "data",
                                        details = Option.Some(body.message)
                                    )
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/auth/forgot/password",
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

    override suspend fun signOut(token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/auth/signOut",
                headers = mapOf("Authorization" to "Bearer $token"),
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/auth/forgot/password",
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

    override suspend fun ping(token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/auth/ping",
                headers = mapOf("Authorization" to "Bearer $token"),
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/auth/forgot/password",
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

    override suspend fun refresh(
        dto: SessionDataDto,
        token: String
    ): DivaResult<SessionResponse, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/auth/refresh",
                headers = mapOf("Authorization" to "Bearer $token"),
                body = dto,
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<SessionResponse> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue(
                                        field = "data",
                                        details = Option.Some(body.message)
                                    )
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/auth/forgot/password",
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
