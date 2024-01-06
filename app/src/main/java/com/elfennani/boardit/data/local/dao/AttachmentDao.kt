package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.AttachmentEntity
import kotlinx.coroutines.flow.Flow

typealias Attachment = AttachmentEntity

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM attachment WHERE boardId = :boardId")
    fun getAttachmentByBoardId(boardId: String) : Flow<List<Attachment>>

    @Upsert
    suspend fun upsertBatch(attachments: List<Attachment>)

    @Query("DELETE FROM attachment WHERE id NOT IN (:ids)")
    suspend fun deleteNotExist(ids: List<Int>)

}