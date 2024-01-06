package com.elfennani.boardit.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.elfennani.boardit.data.models.Board

data class BoardWithInfo(
    @Embedded val board: BoardEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity,

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
    tags = tags.map(TagEntity::asExternalModel)
)
