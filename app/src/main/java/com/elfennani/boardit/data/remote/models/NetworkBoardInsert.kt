package com.elfennani.boardit.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBoardInsert(
    val title: String,
    val note: String?,
    @SerialName("category") val categoryId: Int,
    @SerialName("user_id") val userId: String,
)
