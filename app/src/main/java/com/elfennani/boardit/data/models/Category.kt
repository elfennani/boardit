package com.elfennani.boardit.data.models

import com.elfennani.boardit.data.local.models.SerializableCategory
import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val label: String
)

fun Category.serialize() = SerializableCategory(id, label)