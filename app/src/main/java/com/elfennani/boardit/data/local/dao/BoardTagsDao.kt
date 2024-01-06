package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.BoardTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardTagsDao {

    @Query("SELECT * FROM board_tags")
    fun getAll(): Flow<List<BoardTagsEntity>>

    @Query("SELECT * FROM board_tags WHERE tagId = :tagId")
    fun getByTagId(tagId: Int): Flow<List<BoardTagsEntity>>

    @Query("SELECT * FROM board_tags WHERE boardId = :boardId")
    fun getByBoardId(boardId: Int): Flow<List<BoardTagsEntity>>

    @Upsert
    suspend fun upsertBatchBoardTags(boardTagsEntities: List<BoardTagsEntity>)

    @Query("DELETE FROM board_tags WHERE boardId || \"_\" || tagId NOT IN (:ids)")
    suspend fun deleteNotExist(ids: List<String>)
}