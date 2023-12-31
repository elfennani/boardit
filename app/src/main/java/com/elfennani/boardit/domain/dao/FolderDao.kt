package com.elfennani.boardit.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.domain.entities.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders")
    fun getAll() : Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getFolderById(id: String) : Flow<Folder>

    @Upsert
    suspend fun upsertFolder(folderEntity: Folder)

    @Delete
    suspend fun deleteFolder(folderEntity: Folder)
}