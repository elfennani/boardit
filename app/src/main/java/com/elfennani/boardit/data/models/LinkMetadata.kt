package com.elfennani.boardit.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkMetadata(
    val attachmentId: String,
    val thumbnailUrl: String? = null,
    val title: String? = null,
    val url: String
)
