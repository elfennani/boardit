package com.elfennani.boardit.data.local.models

import kotlinx.serialization.Serializable

@Serializable
data class SerializableData(
    val boards: List<SerializableBoard>,
    val categories: List<SerializableCategory>,
    val tags: List<SerializableTag>,
    val attachments: List<SerializableAttachment>
)
