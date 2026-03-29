package com.diva.user.api.client.preferences

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.preferences.dtos.UserPreferencesDto
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
import io.github.juevigrace.diva.network.client.put
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface UserPreferencesApi {
    suspend fun createPreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError>
    suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError>
}

class UserPreferencesApiImpl(
    private val client: DivaClient
) : UserPreferencesApi {
    override suspend fun createPreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/me/preferences",
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
                                    method = HttpRequestMethod.POST,
                                    url = "/api/user/me/preferences",
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

    override suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.put(
                path = "/api/user/me/preferences",
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
                                    url = "/api/user/me/preferences",
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
