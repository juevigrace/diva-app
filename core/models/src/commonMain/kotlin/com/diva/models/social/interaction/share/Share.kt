package com.diva.models.social.interaction.share

import com.diva.models.social.interaction.Interaction
import com.diva.models.social.share.ShareType

data class Share(
    val interaction: Interaction,
    val shareType: ShareType,
    val caption: String,
)
