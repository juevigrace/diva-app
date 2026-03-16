package com.diva.models.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse<T>(
    @SerialName("items")
    val items: List<T>,
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("page_size")
    val pageSize: Int,
)
