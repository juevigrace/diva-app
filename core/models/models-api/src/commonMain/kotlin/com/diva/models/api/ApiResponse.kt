package com.diva.models.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class ApiResponse<T>(
    @Transient
    val statusCode: Int = 200,
    @SerialName("data")
    val data: T? = null,
    @SerialName("message")
    val message: String,
    @SerialName("timestamp")
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
