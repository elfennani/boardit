package com.elfennani.boardit.data.local.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializableData(
    val boards: List<SerializableBoard> = emptyList(),
    val categories: List<SerializableCategory> = emptyList(),
    val tags: List<SerializableTag> = emptyList(),
    val attachments: List<SerializableAttachment> = emptyList(),
    val deleted: SerializableDeleted = SerializableDeleted()
)
