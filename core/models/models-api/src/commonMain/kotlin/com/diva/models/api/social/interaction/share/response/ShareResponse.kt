package com.diva.models.api.social.interaction.share.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareResponse(
    @SerialName("interaction_id")
    val interactionId: String,
    @SerialName("share_type")
    val shareType: String,
    @SerialName("caption")
    val caption: String
)