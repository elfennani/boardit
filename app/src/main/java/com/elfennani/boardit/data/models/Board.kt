package com.elfennani.boardit.data.models

import java.util.Date

data class Board(
    val id: Int,
    val title: String,
    val category: Category,
    val note: String?,
    val date: Date,
    val tags: List<Tag> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)
