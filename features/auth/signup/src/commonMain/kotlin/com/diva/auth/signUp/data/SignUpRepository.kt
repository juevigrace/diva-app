package com.diva.auth.signUp.data

import com.diva.auth.data.api.client.AuthNetworkClient
import com.diva.database.session.SessionStorage
import com.diva.models.Repository
import com.diva.models.actions.Actions
import com.diva.models.actions.AppActions
import com.diva.models.actions.toAction
import com.diva.models.auth.Session
import com.diva.models.auth.SignUpForm
import io.github.juevigrace.diva.core.DivaResult
import io.github.juevigrace.diva.core.errors.DivaError
import io.github.juevigrace.diva.core.onFailure
import io.github.juevigrace.diva.core.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi

interface SignUpRepository : Repository {
    fun signUp(form: SignUpForm): Flow<DivaResult<Map<Actions, AppActions>, DivaError>>
}

class SignUpRepositoryImpl(
    private val authClient: AuthNetworkClient,
    private val sessionStorage: SessionStorage,
) : SignUpRepository{
    @OptIn(ExperimentalUuidApi::class)
    override fun signUp(form: SignUpForm): Flow<DivaResult<Map<Actions, AppActions>, DivaError>> {
        return flow {
            authClient.signUp(form.toSignUpDto())
                .onFailure { err -> emit(DivaResult.failure(err)) }
                .onSuccess { res ->
                    val session = Session.fromResponse(res.session)
                    sessionStorage
                        .insert(session)
                        .onFailure { err -> emit(DivaResult.failure(err)) }
                        .onSuccess {
                            sessionStorage.update(session.copy(isCurrent = true))
                                .onFailure { err ->
                                    sessionStorage.delete(session.id)
                                        .onFailure { err -> println("panik: ${err.message}") }
                                    emit(DivaResult.failure(err))
                                }
                            val actions = res.actions.map { aRes -> aRes.toAction() }
                            emit(
                                DivaResult.success(actions.associateWith { AppActions.fromAction(it) })
                            )
                        }
                }
        }.flowOn(Dispatchers.Default)
    }
}
