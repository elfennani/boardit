package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("board_tags", primaryKeys = ["boardId", "tagId"])
data class BoardTagsEntity(
    val boardId: Int,
    val tagId: Int,
)
