package com.elfennani.boardit.data.local.models

import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.tencent.mmkv.MMKV
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SerializableBoard(
    val id: String,
    val title: String,
    val category: String?,
    val note: String?,
    val date: String,
    val tags: List<String>,
    val attachments: List<String>
)

fun SerializableBoard.asExternalModel(mmkv: MMKV): Board {
    val categoryString = mmkv.decodeString("category:$category")
    val category: Category? = when {
        categoryString != null && category != null -> Json.decodeFromString<SerializableCategory>(
            categoryString
        ).asExternalModel()

        else -> null
    }

    val tagsModel: List<Tag> = tags.mapNotNull {
        val tagJson = mmkv.decodeString("tag:$it") ?: return@mapNotNull null
        Json.decodeFromString<SerializableTag>(tagJson).asExternalModel()
    }

    val attachmentsMapped: List<Attachment> = attachments.mapNotNull {
        val attachmentJson = mmkv.decodeString("attachment:$it") ?: return@mapNotNull null
        Json.decodeFromString<SerializableAttachment>(attachmentJson).asExternalModel()
    }

    return Board(
        id,
        title,
        category = category,
        note,
        date = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME),
        tags = tagsModel,
        attachments = attachmentsMapped
    )
}