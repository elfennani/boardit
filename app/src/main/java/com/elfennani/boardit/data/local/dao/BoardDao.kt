package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.BoardWithInfo
import com.elfennani.boardit.data.local.entities.BoardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {
    @Transaction
    @Query("SELECT * FROM board")
    fun getBoardAndCategory(): Flow<List<BoardWithInfo>>

    @Upsert
    suspend fun upsertBoard(boardEntity: BoardEntity)
    @Upsert
    suspend fun upsertBatchBoards(boardEntities: List<BoardEntity>)

    @Query("DELETE FROM board WHERE board.id NOT IN (:ids)")
    suspend fun deleteNotExist(ids: List<Int>)
}