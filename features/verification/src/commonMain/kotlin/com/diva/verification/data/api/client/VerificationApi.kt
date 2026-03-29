package com.diva.verification.data.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.verification.dtos.RequestVerificationDto
import com.diva.models.api.verification.dtos.VerificationDto
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

interface VerificationApi {
    suspend fun requestVerification(dto: RequestVerificationDto): DivaResult<Unit, DivaError>
    suspend fun<T> verify(dto: VerificationDto): DivaResult<T, DivaError>
    suspend fun verifyWithAuth(dto: VerificationDto, token: String): DivaResult<Unit, DivaError>
}

class VerificationApiImpl(
    private val client: DivaClient,
) : VerificationApi {
    override suspend fun requestVerification(dto: RequestVerificationDto): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/verification/request",
                body = dto,
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/verification/request",
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

    override suspend fun<T> verify(dto: VerificationDto): DivaResult<T, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/verification",
                body = dto,
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<T> = response.body()
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
                                    url = "/api/verification",
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

    override suspend fun verifyWithAuth(
        dto: VerificationDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/verification/auth",
                body = dto,
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Unit> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/verification/auth",
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

/*
override suspend fun forgotPasswordConfirm(dto: EmailTokenDto): DivaResult<SessionResponse, DivaError> {
    return tryResult(
        onError = { e -> e.toDivaError() }
    ) {
        client.post(
            path = "/api/user/forgot/password/request",
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
                                url = "/api/user/forgot/password",
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
override suspend fun verifyUserEmail(
        dto: EmailTokenDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/me/email/verify",
                headers = mapOf("Authorization" to "Bearer $token"),
                body = dto
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/verify/email",
                                    status = response.status.toHttpStatusCodes(),
                                    details = io.github.juevigrace.diva.core.Option.Some(body.message)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
*/
