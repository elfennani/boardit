package com.elfennani.boardit.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagDto (
    val id: Int,
    val label: String,
    val color: Int?,
    @SerialName("user_id") val userId: String,
    @SerialName("created_at") val createdAt: String
)