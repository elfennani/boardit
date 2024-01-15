package com.elfennani.boardit.data.local.models

import androidx.compose.ui.graphics.Color
import com.elfennani.boardit.data.models.Tag
import kotlinx.serialization.Serializable

@Serializable
data class SerializableTag(
    val id: String,
    val label: String,
    val color: String
)

fun SerializableTag.asExternalModel() = Tag(
    id,
    label,
    Color(android.graphics.Color.parseColor(color))
)
