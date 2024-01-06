package com.elfennani.boardit.data.remote.models

import com.elfennani.boardit.data.local.entities.BoardEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBoard(
    val id: Int,
    @SerialName("created_at") val createdAt: String,
    val title: String,
    val category: Int,
    val note: String?,
    @SerialName("user_id") val userId: String
)

fun NetworkBoard.asEntity() = BoardEntity(
    id = id,
    categoryId = category,
    createdAt = createdAt,
    note = note,
    title = title,
    userId = userId
)