package com.elfennani.boardit.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.elfennani.boardit.data.local.models.SerializableAttachment
import com.elfennani.boardit.data.local.models.SerializableBoard
import com.elfennani.boardit.data.local.models.asExternalModel
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Board
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
    fun insert(board: Board)

    fun update(board: Board)

    fun deleteBoard(board: Board)
}

class BoardRepositoryImpl @Inject constructor(
    private val mmkv: MMKV,
    @ApplicationContext private val context: Context
) : BoardRepository {

    private var _boards = MutableStateFlow(getBoards())
    override val boards: Flow<List<Board>>
        get() = _boards.map { it.map { board -> board.asExternalModel(mmkv) } }

    private fun getBoards(): List<SerializableBoard> {
        val keys = mmkv.allKeys()?.toList()?.filter { it.startsWith("board:") } ?: emptyList()
        return keys.map { Json.decodeFromString<SerializableBoard>(mmkv.decodeString(it)!!) }
    }

    private fun updateBoards() = run { _boards.value = getBoards() }

    override fun deleteBoard(board: Board) {
        mmkv.remove("board:${board.id}")
        updateBoards()
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

                return@map it.copy(url = File(context.filesDir, it.fileName).path)
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

