package com.diva.models.api.pagination.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationInfo(
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
)

@Serializable
data class PaginationResponse<T>(
    @SerialName("items")
    val items: List<T>,
    @SerialName("pagination_info")
    val pagination: PaginationInfo,
)
