package com.diva.user.api.client.preferences

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.preferences.dtos.UserPreferencesDto
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.post
import io.github.juevigrace.diva.network.client.put
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

interface UserPreferencesApi {
    suspend fun createPreferences(
        dto: UserPreferencesDto,
        token: String
    ): Result<Unit>
    suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): Result<Unit>
}

class UserPreferencesApiImpl(
    private val client: DivaClient
) : UserPreferencesApi {
    override suspend fun createPreferences(
        dto: UserPreferencesDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.post(
                path = "/api/user/me/preferences",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Accepted -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me/preferences"),
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

    override suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e }
        ) {
            client.put(
                path = "/api/user/me/preferences",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).fold(
                onSuccess = { response ->
                    when (response.status) {
                        HttpStatusCode.Accepted -> Result.success(Unit)
                        else -> {
                            val body: ApiResponse<Nothing> = response.body()
                            Result.failure(
                                HttpException(
                                    statusCode = Option.of(response.status.value),
                                    url = Option.of("/api/user/me/preferences"),
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
