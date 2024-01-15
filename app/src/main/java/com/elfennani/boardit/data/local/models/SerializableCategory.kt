package com.elfennani.boardit.data.local.models

import com.elfennani.boardit.data.models.Category
import kotlinx.serialization.Serializable

@Serializable
data class SerializableCategory(
    val id: String,
    val label: String
)

fun SerializableCategory.asExternalModel() = Category(id, label)