package com.elfennani.boardit.data.local.models

import kotlinx.serialization.Serializable

@Serializable
data class SerializableDeleted(
    val boards: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val attachments: List<String> = emptyList()
)
