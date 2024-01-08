package com.elfennani.boardit.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBoardTagsInsert(
    @SerialName("board_id") val boardId: Int,
    @SerialName("tag_id") val tagId: Int,
    @SerialName("user_id") val userId: String,
)
