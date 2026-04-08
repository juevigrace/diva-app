package com.diva.user.api.client.preferences

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.preferences.dtos.UserPreferencesDto
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.toDivaNetworkException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
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
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.post(
                path = "/api/user/me/preferences",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UserPreferencesDto.serializer()
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Accepted -> return@tryResult
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me/preferences"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }

    override suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): Result<Unit> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.put(
                path = "/api/user/me/preferences",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token"),
                serializer = UserPreferencesDto.serializer()
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.Accepted -> return@tryResult
                else -> {
                    val body: ApiResponse<Nothing> = response.body()
                    throw HttpException(
                        statusCode = Option.of(response.status.value),
                        url = Option.of("/api/user/me/preferences"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }
}
