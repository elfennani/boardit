package com.elfennani.boardit.data.local.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.models.Tag

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey val id: Int,
    val label: String,
    val userId: String,
    val color: String?
)

fun TagEntity.asExternalModel() = Tag(
    id,
    label,
    color = if (color == null) Color.Gray else Color(android.graphics.Color.parseColor(color))
)