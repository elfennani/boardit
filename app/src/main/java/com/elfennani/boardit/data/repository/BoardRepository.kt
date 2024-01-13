package com.elfennani.boardit.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.MimeTypeMap
import com.elfennani.boardit.data.local.dao.AttachmentDao
import com.elfennani.boardit.data.local.dao.BoardDao
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.EditorAttachment
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.remote.models.NetworkAttachment
import com.elfennani.boardit.data.remote.models.NetworkBoard
import com.elfennani.boardit.data.remote.models.NetworkBoardInsert
import com.elfennani.boardit.data.remote.models.NetworkBoardTags
import com.elfennani.boardit.data.remote.models.NetworkBoardTagsInsert
import com.elfennani.boardit.data.remote.models.asEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.BucketApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject

@Serializable
private data class InsertAttachment(
    val filename: String,
    val url: String,
    val user_id: String,
    val board_id: Int,
    val mime: String,
    val width: Int?,
    val height: Int?
)


interface BoardRepository {
    val boards: Flow<List<Board>>

    suspend fun synchronize()
    suspend fun insert(
        boardInsert: NetworkBoardInsert,
        tags: List<Tag>,
        attachments: List<EditorAttachment>
    )

    suspend fun update(
        board: Board,
        didTagsChange: Boolean,
        attachments: List<EditorAttachment>,
        didOldAttachmentsChange: Boolean
    )

    suspend fun deleteBoard(board: Board)
}

class BoardRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao,
    private val tagRepository: TagRepository,
    private val attachmentDao: AttachmentDao,
    private val supabaseClient: SupabaseClient,
    private val bucketApi: BucketApi,
    @ApplicationContext private val appContext: Context
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

    @SuppressLint("Range")
    override suspend fun insert(
        boardInsert: NetworkBoardInsert,
        tags: List<Tag>,
        attachments: List<EditorAttachment>
    ) {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        val board = supabaseClient
            .from(tableName)
            .insert(boardInsert) {
                select()
            }
            .decodeSingle<NetworkBoard>()

        val localAttachments = attachments.filterIsInstance<EditorAttachment.Local>()

        uploadAttachments(board.id, localAttachments)

        supabaseClient
            .from("board_tags")
            .insert(tags.map { NetworkBoardTagsInsert(board.id, it.id, user.id) })

        tagRepository.synchronize()
        this.synchronize()
    }

    private suspend fun uploadAttachments(boardId: Int, attachments: List<EditorAttachment.Local>) {
        val userId = checkNotNull(supabaseClient.auth.currentUserOrNull()).id

        attachments.forEach { attachment ->
            if (attachment.type is AttachmentType.Link) {
                supabaseClient
                    .from("attachement")
                    .insert(
                        InsertAttachment(
                            attachment.uri.toString(),
                            attachment.uri.toString(),
                            userId,
                            boardId,
                            "other/link",
                            null,
                            null
                        )
                    )
            } else {

                val uri = attachment.uri
                val stream = appContext.contentResolver.openInputStream(uri)!!
                val mime = appContext.contentResolver.getType(uri)!!
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
                val filename = UUID.randomUUID().toString() + "." + extension
                val path = "$userId/$filename"

                bucketApi.upload(
                    path,
                    stream.readBytes(),
                    upsert = false
                )
                stream.close()

                supabaseClient
                    .from("attachement")
                    .insert(
                        InsertAttachment(
                            filename,
                            bucketApi.publicUrl(path),
                            userId,
                            boardId,
                            mime,
                            if (attachment.type is AttachmentType.Image) attachment.type.width else null,
                            if (attachment.type is AttachmentType.Image) attachment.type.height else null
                        )
                    )
            }
        }
    }

    override suspend fun update(
        board: Board,
        didTagsChange: Boolean,
        attachments: List<EditorAttachment>,
        didOldAttachmentsChange: Boolean
    ) {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        supabaseClient
            .from(tableName)
            .update({
                NetworkBoard::title setTo board.title
                NetworkBoard::note setTo board.note
                NetworkBoard::category setTo board.category.id
            }) {
                filter {
                    NetworkBoard::id eq board.id
                }
            }

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

            tagRepository.synchronize()
        }

        val localAttachments = attachments.filterIsInstance<EditorAttachment.Local>()
        uploadAttachments(board.id, localAttachments)

        if (didOldAttachmentsChange) {
            val deletedAttachments =
                board.attachments - attachments.filterIsInstance<EditorAttachment.Remote>()
                    .map { it.attachment }
                    .toSet()

            bucketApi.delete(deletedAttachments.filter { it.type !is AttachmentType.Link }
                .map { it.url.split("/main/")[1] })

            supabaseClient
                .from("attachement")
                .delete {
                    filter {
                        or {
                            deletedAttachments.forEach {
                                NetworkAttachment::id eq it.id
                            }
                        }
                    }
                }
        }

        this.synchronize()
    }

    override suspend fun deleteBoard(board: Board) {
        supabaseClient
            .from(tableName)
            .delete {
                filter {
                    NetworkBoard::id eq board.id
                }
            }

        tagRepository.synchronize()
        this.synchronize()
    }
}

