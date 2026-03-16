package com.diva.models.user.permissions

import com.diva.models.permission.Permission
import com.diva.models.user.User
import io.github.juevigrace.diva.core.Option
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class UserPermission(
    val permission: Permission,
    val grantedBy: User,
    val granted: Boolean,
    val grantedAt: Instant,
    val expiresAt: Option<Instant>,
    val updatedAt: Instant,
)
