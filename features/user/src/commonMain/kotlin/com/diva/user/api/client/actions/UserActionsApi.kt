package com.diva.user.api.client.actions

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.action.event.UserActionsEvents
import com.diva.models.api.user.action.response.ActionResponse
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.ConstraintException
import io.github.juevigrace.diva.core.errors.HttpException
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.toDivaNetworkException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

interface UserActionsApi {
    suspend fun getActions(token: String): Result<List<ActionResponse>>
    suspend fun streamActions(token: String): Flow<Result<UserActionsEvents>>
}

class UserActionsApiImpl(
    private val client: DivaClient
) : UserActionsApi {
    override suspend fun getActions(token: String): Result<List<ActionResponse>> {
        return tryResult(
            onError = { e -> e.toDivaNetworkException() }
        ) {
            val response: HttpResponse = client.get(
                path = "/api/user/me/actions",
                headers = mapOf("Authorization" to "Bearer $token")
            ).getOrThrow()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val body: ApiResponse<List<ActionResponse>> = response.body()
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
                        url = Option.of("/api/user/me/actions"),
                        details = Option.of(body.message)
                    )
                }
            }
        }
    }
    override suspend fun streamActions(token: String): Flow<Result<UserActionsEvents>> {
        return flow {
            client.sse(
                path = "/api/user/me/actions/stream",
                headers = mapOf("Authorization" to "Bearer $token")
            ) {
                incoming.collect { sse ->
                    when (sse.event) {
                        "user-actions-stream" -> {
                            sse.data?.let { str ->
                                val res = Json.decodeFromString<ApiResponse<List<ActionResponse>>>(str)
                                res.data?.let { emit(Result.success(UserActionsEvents.Actions(it))) }
                            }
                        }
                        "user-actions-error" -> {
                            sse.data?.let { str ->
                                val errorRes = Json.decodeFromString<ApiResponse<Unit>>(str)
                                emit(
                                    Result.failure(
                                        HttpException(
                                            statusCode = Option.of(500),
                                            url = Option.of("/api/user/me/actions/stream"),
                                            details = Option.of(errorRes.message)
                                        )
                                    )
                                )
                            }
                        }
                        "user-actions-end" -> emit(Result.success(UserActionsEvents.End))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
