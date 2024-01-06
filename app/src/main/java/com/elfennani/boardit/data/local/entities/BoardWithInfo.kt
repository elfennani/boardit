package com.elfennani.boardit.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.elfennani.boardit.data.models.Board
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

data class BoardWithInfo(
    @Embedded val board: BoardEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val attachments: List<AttachmentEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(BoardTagsEntity::class, parentColumn = "boardId", entityColumn = "tagId")
    )
    val tags: List<TagEntity>
)

fun BoardWithInfo.asExternalModel() = Board(
    id = board.id,
    title = board.title,
    note = board.note,
    category = category.asExternalModel(),
    date = Date.from(OffsetDateTime.parse(board.createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()),
    tags = tags.map(TagEntity::asExternalModel),
    attachments = attachments.map(AttachmentEntity::asExternalModel)
)
