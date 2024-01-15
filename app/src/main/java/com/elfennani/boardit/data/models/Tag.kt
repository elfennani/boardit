package com.elfennani.boardit.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.elfennani.boardit.data.local.models.SerializableTag
import java.util.UUID

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val color: Color
)

fun Tag.getColor(): Color = color

@OptIn(ExperimentalStdlibApi::class)
fun Tag.serialize() = SerializableTag(
    id,
    label,
    color.toArgb()
        .toHexString(HexFormat.UpperCase)
        .replaceFirst("FF", "#")
)