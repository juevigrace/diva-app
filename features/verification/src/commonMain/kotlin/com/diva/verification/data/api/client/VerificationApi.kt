package com.diva.verification.data.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.verification.dtos.RequestVerificationDto
import com.diva.models.api.verification.dtos.VerificationDto
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.post
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface VerificationApi {
    suspend fun requestVerification(dto: RequestVerificationDto): Result<Unit>
    suspend fun<T> verify(dto: VerificationDto): Result<T>
    suspend fun verifyWithAuth(dto: VerificationDto, token: String): Result<Unit>
}

class VerificationApiImpl(
    private val client: DivaClient,
) : VerificationApi {
    override suspend fun requestVerification(dto: RequestVerificationDto): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.post(
                path = "/api/verification/request",
                body = dto,
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/verification/request"),
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

    override suspend fun<T> verify(dto: VerificationDto): Result<T> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.post(
                path = "/api/verification",
                body = dto,
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> {
                            val body: ApiResponse<T> = response.body()
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
                                    url = Option.of("/api/verification"),
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

    override suspend fun verifyWithAuth(
        dto: VerificationDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.post(
                path = "/api/verification/auth",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.OK -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Unit> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/verification/auth"),
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
