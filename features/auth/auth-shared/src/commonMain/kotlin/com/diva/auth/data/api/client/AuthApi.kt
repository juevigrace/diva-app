package com.diva.auth.data.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.auth.forgot.password.dtos.UpdatePasswordDto
import com.diva.models.api.auth.session.dtos.SessionDataDto
import com.diva.models.api.auth.session.response.SessionResponse
import com.diva.models.api.auth.signin.dto.SignInDto
import com.diva.models.api.auth.signup.dto.SignUpDto
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.toDivaNetworkException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

interface AuthApi {
    suspend fun signIn(dto: SignInDto): Result<SessionResponse>
    suspend fun signUp(dto: SignUpDto): Result<SessionResponse>
    suspend fun signOut(token: String): Result<Unit>
    suspend fun ping(token: String): Result<Unit>
    suspend fun refresh(dto: SessionDataDto, token: String): Result<SessionResponse>
    suspend fun forgotPasswordReset(dto: UpdatePasswordDto, token: String): Result<Unit>
}

class AuthApiImpl(
    private val client: DivaClient
) : AuthApi {
    override suspend fun signIn(dto: SignInDto): Result<SessionResponse> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/auth/signIn",
                body = dto,
                serializer = SignInDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body: ApiResponse<SessionResponse> = response.body()
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
                        url = Option.of("/api/auth/signIn"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun signUp(dto: SignUpDto): Result<SessionResponse> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/auth/signUp",
                body = dto,
                serializer = SignUpDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Created -> {
                    val body: ApiResponse<SessionResponse> = response.body()
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
                        url = Option.of("/api/auth/signUp"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun signOut(token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/auth/signOut",
                headers = mapOf("Authorization" to "Bearer $token"),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/auth/signOut"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun ping(token: String): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/auth/ping",
                headers = mapOf("Authorization" to "Bearer $token"),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/auth/ping"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun refresh(
        dto: SessionDataDto,
        token: String
    ): Result<SessionResponse> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/auth/refresh",
                headers = mapOf("Authorization" to "Bearer $token"),
                body = dto,
                serializer = SessionDataDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body: ApiResponse<SessionResponse> = response.body()
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
                        url = Option.of("/api/auth/refresh"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun forgotPasswordReset(
        dto: UpdatePasswordDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.patch(
                path = "/api/auth/forgot/password",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UpdatePasswordDto.serializer(),
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> return@tryResult
                else -> {
                    val body: ApiResponse<Unit> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/forgot/password"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }
}
