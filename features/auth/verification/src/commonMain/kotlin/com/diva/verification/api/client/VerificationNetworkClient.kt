package com.diva.verification.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.verification.dtos.EmailTokenDto
import io.github.juevigrace.diva.core.DivaResult
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

interface VerificationNetworkClient {
    suspend fun verifyEmailToken(dto: EmailTokenDto): DivaResult<Unit, DivaError>
    suspend fun verifyUserEmail(dto: EmailTokenDto, token: String): DivaResult<Unit, DivaError>
}

class VerificationNetworkClientImpl(
    private val client: DivaClient
) : VerificationNetworkClient {
    override suspend fun verifyEmailToken(dto: EmailTokenDto): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/verify",
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
                                    url = "/api/verify",
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

    override suspend fun verifyUserEmail(
        dto: EmailTokenDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/verify/email",
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
}

