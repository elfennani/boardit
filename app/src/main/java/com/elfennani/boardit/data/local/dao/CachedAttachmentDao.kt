package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.CachedAttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedAttachmentDao {
    @Query("SELECT * FROM cached_attachment")
    fun getAll(): Flow<List<CachedAttachmentEntity>>

    @Upsert
    suspend fun upsertCachedAttachment(cachedAttachmentEntity: CachedAttachmentEntity)
}