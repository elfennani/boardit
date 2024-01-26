package com.elfennani.boardit.data.local.models

import com.elfennani.boardit.data.models.Category
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SerializableCategory(
    val id: String,
    val label: String,
    val modified: String
)

fun SerializableCategory.asExternalModel() =
    Category(id, label, LocalDateTime.parse(modified, DateTimeFormatter.ISO_DATE_TIME))