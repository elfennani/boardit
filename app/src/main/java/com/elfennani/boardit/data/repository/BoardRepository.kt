package com.elfennani.boardit.data.repository

import com.elfennani.boardit.data.local.dao.AttachmentDao
import com.elfennani.boardit.data.local.dao.BoardDao
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.remote.models.NetworkAttachment
import com.elfennani.boardit.data.remote.models.NetworkBoard
import com.elfennani.boardit.data.remote.models.asEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface BoardRepository {
    val boards: Flow<List<Board>>

    suspend fun synchronize()
}

class BoardRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao,
    private val attachmentDao: AttachmentDao,
    private val supabaseClient: SupabaseClient,
    private val bucket: BucketApi
) : BoardRepository {
    override val boards: Flow<List<Board>>
        get() = boardDao.getBoardAndCategory().map { it.map { it.asExternalModel() } }

    private val tableName = "board"

    override suspend fun synchronize() {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        val boardsNetwork = supabaseClient
            .from(tableName)
            .select {
                filter {
                    NetworkBoard::userId eq user.id
                }
            }.decodeList<NetworkBoard>()
        val attachmentNetwork = supabaseClient
            .from("attachement")
            .select {
                filter {
                    NetworkAttachment::userId eq user.id
                }
            }.decodeList<NetworkAttachment>()

        attachmentDao.deleteNotExist(attachmentNetwork.map { it.id })
        attachmentDao.upsertBatch(attachmentNetwork.map(NetworkAttachment::asEntity))
        boardDao.deleteNotExist(boardsNetwork.map { it.id })
        boardDao.upsertBatchBoards(boardsNetwork.map { it.asEntity() })
    }
}

