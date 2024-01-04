package com.elfennani.boardit.data.remote.models

import com.elfennani.boardit.data.local.entities.TagEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTag (
    val id: Int,
    val label: String,
    val color: String?,
    @SerialName("user_id") val userId: String,
    @SerialName("created_at") val createdAt: String
)

fun NetworkTag.asEntity() = TagEntity(id, label, userId, color)