package com.diva.user.api.client.actions

import com.diva.models.api.ApiResponse
import com.diva.models.api.user.action.event.UserActionsEvents
import com.diva.models.api.user.action.response.ActionResponse
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.errors.toDivaError
import io.github.juevigrace.diva.core.flatMap
import io.github.juevigrace.diva.core.network.HttpRequestMethod
import io.github.juevigrace.diva.core.network.HttpStatusCodes
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.get
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

interface UserActionsApi {
    suspend fun getActions(token: String): DivaResult<List<ActionResponse>, DivaError>
    suspend fun streamActions(token: String): Flow<DivaResult<UserActionsEvents, DivaError>>
}

class UserActionsApiImpl(
    private val client: DivaClient
) : UserActionsApi {
    override suspend fun getActions(token: String): DivaResult<List<ActionResponse>, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(path = "/api/user/me/actions").flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<List<ActionResponse>> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Unit> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user/{id}",
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
    override suspend fun streamActions(token: String): Flow<DivaResult<UserActionsEvents, DivaError>> {
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
                                res.data?.let { emit(DivaResult.success(UserActionsEvents.Actions(it))) }
                            }
                        }
                        "user-actions-error" -> {
                            sse.data?.let { str ->
                                val errorRes = Json.decodeFromString<ApiResponse<Unit>>(str)
                                emit(
                                    DivaResult.failure(
                                        DivaError(
                                            ErrorCause.Network.Error(
                                                HttpRequestMethod.GET,
                                                "/api/user/me/actions/stream",
                                                HttpStatusCodes.InternalServerError,
                                                details = Option.Some(errorRes.message)
                                            )
                                        )
                                    )
                                )
                            }
                        }
                        "user-actions-end" -> emit(DivaResult.success(UserActionsEvents.End))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
