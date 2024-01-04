package com.elfennani.boardit.data.remote.models

import com.elfennani.boardit.data.local.entities.CategoryEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCategory(
    val id: Int,
    val label: String,
    @SerialName("user_id") val userId: String,
    @SerialName("created_at") val createdAt: String
)

fun NetworkCategory.asEntity() = CategoryEntity(
    id = id,
    label = label,
    userId = userId
)