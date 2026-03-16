package com.diva.user.api.client

import com.diva.models.api.ApiResponse
import com.diva.models.api.PaginationResponse
import com.diva.models.api.auth.dtos.PasswordUpdateDto
import com.diva.models.api.user.dtos.CreateUserDto
import com.diva.models.api.user.dtos.EmailTokenDto
import com.diva.models.api.user.dtos.UpdateUserDto
import com.diva.models.api.user.dtos.UserEmailDto
import com.diva.models.api.user.preferences.dtos.UserPreferencesDto
import com.diva.models.api.user.response.UserResponse
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.errors.ErrorCause
import io.github.juevigrace.diva.core.errors.toDivaError
import io.github.juevigrace.diva.core.flatMap
import io.github.juevigrace.diva.core.network.HttpRequestMethod
import io.github.juevigrace.diva.core.tryResult
import io.github.juevigrace.diva.network.client.DivaClient
import io.github.juevigrace.diva.network.client.delete
import io.github.juevigrace.diva.network.client.get
import io.github.juevigrace.diva.network.client.patch
import io.github.juevigrace.diva.network.client.post
import io.github.juevigrace.diva.network.client.put
import io.github.juevigrace.diva.network.client.toHttpStatusCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

// TODO: break down
interface UserNetworkClient {
    suspend fun getAll(
        page: Int,
        pageSize: Int
    ): DivaResult<PaginationResponse<UserResponse>, DivaError>
    suspend fun getById(id: String): DivaResult<UserResponse, DivaError>
    suspend fun create(dto: CreateUserDto, token: String): DivaResult<String, DivaError>
    suspend fun update(id: String, dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError>
    suspend fun delete(id: String, token: String): DivaResult<Unit, DivaError>
    suspend fun updateMe(dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError>
    suspend fun deleteMe(token: String): DivaResult<Unit, DivaError>
    suspend fun forgotPasswordRequest(dto: UserEmailDto): DivaResult<Unit, DivaError>
    suspend fun forgotPasswordConfirm(dto: EmailTokenDto): DivaResult<Unit, DivaError>
    suspend fun forgotPasswordReset(dto: PasswordUpdateDto, token: String): DivaResult<Unit, DivaError>
    suspend fun requestEmailUpdate(
        dto: UserEmailDto,
        token: String
    ): DivaResult<Unit, DivaError>
    suspend fun confirmEmailUpdate(
        dto: EmailTokenDto,
        token: String
    ): DivaResult<Unit, DivaError>
    suspend fun updateEmail(
        dto: UserEmailDto,
        token: String
    ): DivaResult<Unit, DivaError>
    suspend fun createPreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError>
    suspend fun updatePreferences(
        dto: UserPreferencesDto,
        token: String
    ): DivaResult<Unit, DivaError>
}

class UserNetworkClientImpl(
    private val client: DivaClient
) : UserNetworkClient {
    override suspend fun getAll(
        page: Int,
        pageSize: Int,
    ): DivaResult<PaginationResponse<UserResponse>, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(path = "/api/user").flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<PaginationResponse<UserResponse>> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.GET,
                                    url = "/api/user",
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

    override suspend fun getById(id: String): DivaResult<UserResponse, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.get(path = "/api/user/$id").flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val body: ApiResponse<UserResponse> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
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

    override suspend fun create(
        dto: CreateUserDto,
        token: String
    ): DivaResult<String, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.Created -> {
                        val body: ApiResponse<String> = response.body()
                        body.data?.let { data -> DivaResult.success(data) }
                            ?: DivaResult.failure(
                                DivaError(
                                    cause = ErrorCause.Validation.MissingValue("data", Option.Some(body.message)),
                                )
                            )
                    }
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/user",
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

    override suspend fun update(
        id: String,
        dto: UpdateUserDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.put(
                path = "/api/user/$id",
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

    override suspend fun delete(id: String, token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.delete(
                path = "/api/user/$id",
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.NoContent -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
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

    override suspend fun updateMe(dto: UpdateUserDto, token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.put(
                path = "/api/user/me",
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
                                    url = "/api/user/me",
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

    override suspend fun deleteMe(token: String): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.delete(
                path = "/api/user/me",
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.NoContent -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/user/me",
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

    override suspend fun requestEmailUpdate(
        dto: UserEmailDto,
        token: String,
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/me/email/request",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/user/me/email/request",
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

    override suspend fun confirmEmailUpdate(
        dto: EmailTokenDto,
        token: String,
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/me/email/confirm",
                body = dto,
                headers = mapOf("Authorization" to "Bearer $token")
            ).flatMap { response ->
                when (response.status) {
                    HttpStatusCode.OK -> DivaResult.success(Unit)
                    else -> {
                        val body: ApiResponse<Nothing> = response.body()
                        DivaResult.failure(
                            DivaError(
                                cause = ErrorCause.Network.Error(
                                    method = HttpRequestMethod.POST,
                                    url = "/api/user/me/email/confirm",
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

    override suspend fun updateEmail(
        dto: UserEmailDto,
        token: String,
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.patch(
                path = "/api/user/me/email",
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
                                    method = HttpRequestMethod.PATCH,
                                    url = "/api/user/me/email",
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

    override suspend fun forgotPasswordRequest(dto: UserEmailDto): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/forgot/password/request",
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

    override suspend fun forgotPasswordConfirm(dto: EmailTokenDto): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.post(
                path = "/api/user/forgot/password/request",
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

    override suspend fun forgotPasswordReset(
        dto: PasswordUpdateDto,
        token: String
    ): DivaResult<Unit, DivaError> {
        return tryResult(
            onError = { e -> e.toDivaError() }
        ) {
            client.patch(
                path = "/api/user/forgot/password",
                body = dto,
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
}
