package com.diva.auth.session.data

import com.diva.database.session.SessionStorage

interface SessionRepository {
}

class SessionRepositoryImpl(
    private val sessionStorage: SessionStorage
) : SessionRepository {
}
