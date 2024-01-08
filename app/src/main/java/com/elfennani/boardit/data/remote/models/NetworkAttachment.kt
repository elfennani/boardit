package com.elfennani.boardit.data.remote.models

import com.elfennani.boardit.data.local.entities.AttachmentEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkAttachment(
    val id: Int,
    val filename: String,
    val url: String,
    val mime: String,
    val width: Int?,
    val height: Int?,
    @SerialName("user_id") val userId: String,
    @SerialName("board_id") val boardId: Int,
    @SerialName("created_at") val createdAt: String
)

fun NetworkAttachment.asEntity() = AttachmentEntity(
    id = id,
    fileName = filename,
    url=url,
    userId = userId,
    boardId = boardId,
    mime = mime,
    width = width,
    height = height,
)