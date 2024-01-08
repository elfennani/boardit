package com.elfennani.boardit.data.repository

import com.elfennani.boardit.data.local.dao.AttachmentDao
import com.elfennani.boardit.data.local.dao.BoardDao
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.remote.models.NetworkAttachment
import com.elfennani.boardit.data.remote.models.NetworkBoard
import com.elfennani.boardit.data.remote.models.NetworkBoardInsert
import com.elfennani.boardit.data.remote.models.NetworkBoardTags
import com.elfennani.boardit.data.remote.models.NetworkBoardTagsInsert
import com.elfennani.boardit.data.remote.models.asEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.BucketApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface BoardRepository {
    val boards: Flow<List<Board>>

    suspend fun synchronize()
    suspend fun insert(boardInsert: NetworkBoardInsert, tags: List<Tag>)
    suspend fun update(board: Board, didTagsChange: Boolean)
}

class BoardRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao,
    private val tagRepository: TagRepository,
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

    override suspend fun insert(boardInsert: NetworkBoardInsert, tags: List<Tag>) {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        val board = supabaseClient
            .from(tableName)
            .insert(boardInsert) {
                select()
            }
            .decodeSingle<NetworkBoard>()

        supabaseClient
            .from("board_tags")
            .insert(tags.map { NetworkBoardTagsInsert(board.id, it.id, user.id) })

        tagRepository.synchronize()
        this.synchronize()
    }

    override suspend fun update(board: Board, didTagsChange: Boolean) {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        supabaseClient
            .from(tableName)
            .update({
                NetworkBoard::title setTo board.title
                NetworkBoard::note setTo board.note
                NetworkBoard::category setTo board.category.id
            }) {
                select()
                filter {
                    NetworkBoard::id eq board.id
                }
            }.decodeSingle<NetworkBoard>()

        if (didTagsChange) {
            if (board.tags.isNotEmpty())
                supabaseClient
                    .from("board_tags")
                    .upsert(board.tags.map { NetworkBoardTagsInsert(board.id, it.id, user.id) })

            supabaseClient
                .from("board_tags")
                .delete {
                    filter {
                        if (board.tags.isEmpty())
                            NetworkBoardTags::boardId eq board.id
                        else
                            and {
                                NetworkBoardTags::boardId eq board.id
                                and {
                                    board.tags.forEach {
                                        NetworkBoardTags::tagId neq it.id
                                    }
                                }
                            }
                    }
                }
            /**
             * boardId=HelloWorld && (tagId !== inspiration || tagId !== themes)
             */

            tagRepository.synchronize()
        }

        this.synchronize()
    }
}

