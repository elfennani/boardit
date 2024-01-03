package com.elfennani.boardit.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.models.Category

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val label: String,
    val userId: String,
)

fun CategoryEntity.asExternalModel() = Category(
    id= id,
    label = label
)