package com.elfennani.boardit.data.models

import com.elfennani.boardit.data.local.models.SerializableBoard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Board(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: Category?,
    val note: String?,
    val created: LocalDateTime = LocalDateTime.now(),
    val modified: LocalDateTime,
    val tags: List<Tag> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)

fun Board.serialize() = SerializableBoard(
    id,
    title,
        category = category?.id,
    note,
    created = created.format(DateTimeFormatter.ISO_DATE_TIME),
    modified = modified.format(DateTimeFormatter.ISO_DATE_TIME),
    attachments = attachments.map { it.id },
    tags = tags.map { it.id }
)