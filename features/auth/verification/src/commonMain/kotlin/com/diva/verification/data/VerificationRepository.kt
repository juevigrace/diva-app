package com.diva.verification.data

import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.verification.VerificationForm
import com.diva.verification.api.client.VerificationNetworkClient
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi

interface VerificationRepository : Repository {
    fun verifyEmailToken(form: VerificationForm): Flow<DivaResult<Unit, DivaError>>
    fun verifyUserEmail(form: VerificationForm): Flow<DivaResult<Unit, DivaError>>
}

// TODO: i need to store the verification state if the action was cancelled or just not processed
class VerificationRepositoryImpl(
    private val sessionStorage: SessionStorage,
    private val verificationClient: VerificationNetworkClient,
) : VerificationRepository {
    override fun verifyEmailToken(form: VerificationForm): Flow<DivaResult<Unit, DivaError>> {
        return flow {
            verificationClient.verifyEmailToken(form.toEmailTokenDto())
                .onFailure { err ->
                    emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }.flowOn(Dispatchers.Default)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun verifyUserEmail(form: VerificationForm): Flow<DivaResult<Unit, DivaError>> {
        return withSession(sessionStorage::getCurrentSession) { session ->
            verificationClient.verifyUserEmail(form.toEmailTokenDto(), session.accessToken)
                .onFailure { err ->
                    sessionStorage.delete(session.id)
                        .onFailure { err -> println("panik: ${err.message}") }
                    emit(DivaResult.failure(err)) }
                .onSuccess { emit(DivaResult.success(Unit)) }
        }
    }
}
