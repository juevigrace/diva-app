package com.diva.models.social.interaction.comment

import com.diva.models.social.interaction.Interaction
import io.github.juevigrace.diva.core.Option

data class Comment(
    val interaction: Interaction,
    val replyTo: Option<Comment> = Option.None,
    val content: String,
    val editedAt: Option<Long> = Option.None,
)
