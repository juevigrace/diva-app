package com.diva.models.user

import com.diva.models.api.user.response.UserResponse
import com.diva.models.collection.Collection
import com.diva.models.roles.Role
import com.diva.models.social.interaction.share.Share
import com.diva.models.social.post.Post
import com.diva.models.user.permissions.UserPermission
import com.diva.models.user.preferences.UserPreferences
import io.github.juevigrace.diva.core.Option
import io.github.juevigrace.diva.core.getOrElse
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: Uuid,
    val email: String = "",
    val username: String = "",
    val passwordHash: Option<String> = Option.None,
    val birthDate: Instant = Clock.System.now(),
    val phoneNumber: String = "",
    val alias: String = "",
    val avatar: String = "",
    val bio: String = "",
    val userVerified: Boolean = false,
    val role: Role = Role.USER,
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val deletedAt: Option<Instant> = Option.None,
    val permissions: List<UserPermission> = emptyList(),
    val followers: List<User> = emptyList(),
    val following: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val shares: List<Share> = emptyList(),
    val collections: List<Collection> = emptyList(),
) {
    fun toResponse(): UserResponse {
        return UserResponse(
            id = id.toString(),
            email = email,
            username = username,
            birthDate = birthDate.toEpochMilliseconds(),
            phoneNumber = phoneNumber,
            alias = alias,
            avatar = avatar,
            bio = bio,
            userVerified = userVerified,
            createdAt = createdAt.toEpochMilliseconds(),
            updatedAt = updatedAt.toEpochMilliseconds(),
            deletedAt = deletedAt.getOrElse { null }?.toEpochMilliseconds(),
        )
    }
    companion object {
        fun fromResponse(response: UserResponse): User {
            return User(
                id = Uuid.parse(response.id),
                email = response.email,
                username = response.username,
                birthDate = Instant.fromEpochMilliseconds(response.birthDate),
                phoneNumber = response.phoneNumber,
                alias = response.alias,
                avatar = response.avatar,
                bio = response.bio,
                userVerified = response.userVerified,
                createdAt = Instant.fromEpochMilliseconds(response.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(response.updatedAt),
                deletedAt = Option.of(response.deletedAt?.let { value -> Instant.fromEpochMilliseconds(value) }),
            )
        }
    }
}
