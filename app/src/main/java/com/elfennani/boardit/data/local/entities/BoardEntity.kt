package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.remote.models.NetworkCategory
import kotlinx.serialization.SerialName

@Entity(tableName = "board")
data class BoardEntity(
    @PrimaryKey val id: Int,
    val createdAt: String,
    val title: String,
    val categoryId: Int,
    val note: String?,
    val userId: String
)
