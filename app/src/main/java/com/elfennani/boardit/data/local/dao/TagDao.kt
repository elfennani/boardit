package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tag")
    fun getAll() : Flow<List<TagEntity>>
    @Query("SELECT * FROM tag WHERE id = :id")
    fun getTagById(id: String) : Flow<TagEntity>
    @Upsert
    suspend fun upsertTag(categoryEntity: TagEntity)
    @Delete
    suspend fun deleteTag(categoryEntity: TagEntity)
}