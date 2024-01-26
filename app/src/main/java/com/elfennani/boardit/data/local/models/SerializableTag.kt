package com.elfennani.boardit.data.local.models

import androidx.compose.ui.graphics.Color
import com.elfennani.boardit.data.models.Tag
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SerializableTag(
    val id: String,
    val label: String,
    val color: String,
    val modified: String,
)

fun SerializableTag.asExternalModel() = Tag(
    id,
    label,
    Color(android.graphics.Color.parseColor(color)),
    LocalDateTime.parse(modified, DateTimeFormatter.ISO_DATE_TIME)
)
