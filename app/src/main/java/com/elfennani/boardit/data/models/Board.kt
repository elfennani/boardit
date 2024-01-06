package com.elfennani.boardit.data.models

data class Board(
    val id: Int,
    val title: String,
    val category: Category,
    val note: String?,
    val tags: List<Tag> = emptyList()
)
