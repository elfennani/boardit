package com.elfennani.boardit.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.elfennani.boardit.data.local.models.SerializableAttachment
import com.elfennani.boardit.data.local.models.SerializableBoard
import com.elfennani.boardit.data.local.models.asExternalModel
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.MergeChanges
import com.elfennani.boardit.data.models.serialize
import com.tencent.mmkv.MMKV
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject


interface BoardRepository {
    val boards: Flow<List<Board>>
    val deletedBoards: List<String>
    val deletedAttachments: List<String>
    fun insert(board: Board)

    fun getBoards(): List<SerializableBoard>

    fun update(board: Board)

    fun deleteBoard(board: Board)
    fun deleteBoard(id: String)
    fun getAttachments(): List<SerializableAttachment>
    fun merge(changes: MergeChanges<SerializableBoard>)
}

class BoardRepositoryImpl @Inject constructor(
    private val mmkv: MMKV,
    @ApplicationContext private val context: Context
) : BoardRepository {

    private var _boards = MutableStateFlow(getBoards())
    override val boards: Flow<List<Board>>
        get() = _boards.map { it.map { board -> board.asExternalModel(mmkv) } }

    override fun getBoards(): List<SerializableBoard> {
        val keys = mmkv.allKeys()?.toList()?.filter { it.startsWith("board:") } ?: emptyList()
        return keys.map { Json.decodeFromString<SerializableBoard>(mmkv.decodeString(it)!!) }
    }

    override fun getAttachments(): List<SerializableAttachment> {
        val keys = mmkv.allKeys()?.toList()?.filter { it.startsWith("attachment:") } ?: emptyList()
        return keys.map { Json.decodeFromString<SerializableAttachment>(mmkv.decodeString(it)!!) }
    }

    override val deletedBoards: List<String>
        get() = mmkv
            .allKeys()
            ?.toList()
            ?.filter { it.startsWith("deleted-board:") }
            ?.map { mmkv.decodeString(it)!! }
            ?: emptyList()

    override val deletedAttachments: List<String>
        get() = mmkv
            .allKeys()
            ?.toList()
            ?.filter { it.startsWith("deleted-attachment:") }
            ?.map { mmkv.decodeString(it)!! }
            ?: emptyList()

    private fun updateBoards() = run { _boards.value = getBoards() }

    override fun deleteBoard(board: Board) {
        board.attachments.forEach {
            mmkv.remove("attachment:${it.id}")
            mmkv.encode("deleted-attachment:${it.id}", it.id)
            // TODO: Delete Files from storage
        }
        mmkv.remove("board:${board.id}")
        mmkv.encode("deleted-board:${board.id}", board.id)
        updateBoards()
    }

    override fun deleteBoard(id: String) {
        val data = mmkv.decodeString("board:$id") ?: return
        deleteBoard(Json.decodeFromString<SerializableBoard>(data).asExternalModel(mmkv))
    }

    override fun merge(changes: MergeChanges<SerializableBoard>) {
        changes.added.forEach {
            insert(it.asExternalModel(mmkv))
        }
    }

    override fun insert(board: Board) {
        saveAttachments(board.attachments, board.id)
        mmkv.encode("board:${board.id}", Json.encodeToString(board.serialize()))
        updateBoards()
    }

    private fun saveAttachments(attachments: List<Attachment>, boardId: String) {
        val toDeleteAttachment = getBoards()
            .map { it.asExternalModel(mmkv) }
            .find { it.id == boardId }
            ?.attachments
            ?.filter { !it.url.startsWith("content://") && attachments.find { att -> att.id == it.id } == null }

        toDeleteAttachment?.forEach {
            File(it.url).delete()
            mmkv.remove("attachment:${it.id}")
        }

        val newAttachments = attachments.map {
            if (it.url.startsWith("content://")) {

                val bytes = context.contentResolver.openInputStream(it.url.toUri())
                    .use { stream -> stream!!.readBytes() }

                context.openFileOutput(it.fileName, Context.MODE_PRIVATE).use { stream ->
                    stream.write(bytes)
                }
                val file = File(context.filesDir, it.fileName)
                val extension = if (file.extension.isEmpty()) "" else "." + file.extension

                return@map it.copy(url = file.path, fileName = "${it.id}$extension")
            } else {
                return@map it
            }
        }

        newAttachments.forEach {
            mmkv.encode(
                "attachment:${it.id}",
                Json.encodeToString<SerializableAttachment>(it.serialize())
            )
        }
    }

    override fun update(board: Board) {
        Log.d("BOARDID", board.id)
        saveAttachments(board.attachments, board.id)
        mmkv.encode("board:${board.id}", Json.encodeToString(board.serialize()))
        updateBoards()
    }
}

