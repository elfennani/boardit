package com.elfennani.boardit.data.remote.models

import com.elfennani.boardit.data.local.entities.BoardTagsEntity
import com.elfennani.boardit.data.models.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBoardTags(
    @SerialName("board_id") val boardId: Int,
    @SerialName("tag_id") val tagId: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_id") val userId: String
)

fun NetworkBoardTags.asEntity() = BoardTagsEntity(
    boardId,
    tagId
)

fun NetworkBoardTags.combineIds() = "${this.boardId}_${this.tagId}"