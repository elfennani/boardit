package com.elfennani.boardit.data.models

import com.elfennani.boardit.data.local.models.SerializableCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val modified: LocalDateTime,
)

fun Category.serialize() =
    SerializableCategory(id, label, modified.format(DateTimeFormatter.ISO_DATE_TIME))