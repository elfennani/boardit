package com.elfennani.boardit.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val label: String,
    @SerialName("note_count") val noteCount: Int
)
