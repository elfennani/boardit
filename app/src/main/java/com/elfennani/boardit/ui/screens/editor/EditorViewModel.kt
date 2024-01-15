package com.elfennani.boardit.ui.screens.editor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @SuppressLint("StaticFieldLeak")
@Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val boardRepository: BoardRepository,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    init {
        savedStateHandle.get<Intent>(NavController.KEY_DEEP_LINK_INTENT)
            ?.getStringExtra(Intent.EXTRA_TEXT)
            ?.let { Log.d("RECEIVEDINTENT2", it.toString()) }
    }

    private val boardId = savedStateHandle.get<String>("id")
    private val board = runBlocking {
        if (boardId != null) boardRepository.boards.first()
            .find { it.id == boardId } else null
    }

    private val stateInitialValue = getInitialValue()

    private fun getInitialValue(): EditorScreenState {
        if (board != null)
            return EditorScreenState(
                titleTextFieldValue = TextFieldValue(board.title),
                bodyTextFieldValue = TextFieldValue(board.note ?: ""),
                selectedCategory = board.category,
                selectedTags = board.tags.toSet(),
                attachments = board.attachments,
                isNew = false
            )

        val intent = savedStateHandle.get<Intent>(NavController.KEY_DEEP_LINK_INTENT)
        return when {
            intent?.action == Intent.ACTION_SEND && intent.type == "text/plain" ->
                EditorScreenState(
                    bodyTextFieldValue = TextFieldValue(
                        intent.getStringExtra(
                            Intent.EXTRA_TEXT
                        ) ?: ""
                    )
                )

            intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true ->
                EditorScreenState(
                    attachments = listOf(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri).toAttachmentsImages()
                )

            intent?.action == Intent.ACTION_SEND_MULTIPLE && intent.type?.startsWith("image/") == true -> {
                val list = intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)
                return EditorScreenState(
                    attachments = list?.map { (it as Uri) }?.toAttachmentsImages()
                        ?: emptyList()
                )
            }

            else -> EditorScreenState()
        }
    }

    private val _state = MutableStateFlow(stateInitialValue)

    val state =
        combine(
            categoryRepository.categories,
            tagRepository.tags,
            _state
        ) { categories, tags, editorScreenState ->
            editorScreenState.copy(categories = categories, tags = tags)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            stateInitialValue
        )

    fun event(event: EditorScreenEvent) {
        _state.value = when (event) {
            is EditorScreenEvent.ModifyTitle -> _state.value.copy(titleTextFieldValue = event.textFieldValue)
            is EditorScreenEvent.ModifyBody -> _state.value.copy(bodyTextFieldValue = event.textFieldValue)
            is EditorScreenEvent.OpenModal -> _state.value.copy(modalState = event.modalState)
            is EditorScreenEvent.CloseModal -> _state.value.copy(modalState = ModalState.CLOSED)
            is EditorScreenEvent.SelectCategory -> _state.value.copy(selectedCategory = event.category)
            is EditorScreenEvent.SelectTag -> {
                val tag = event.tag
                val selectedTags = _state.value.selectedTags
                val newSelected = if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }

                _state.value.copy(selectedTags = newSelected)
            }

            is EditorScreenEvent.Save -> {
                save()
                _state.value
            }

            is EditorScreenEvent.DeleteAttachment -> _state.value.copy(attachments = _state.value.attachments - event.attachment)
            is EditorScreenEvent.DeleteBoard -> {
                delete()
                _state.value
            }

            is EditorScreenEvent.PickImages -> {
                _state.value.copy(
                    attachments = _state.value.attachments + event.uris.toAttachmentsImages()
                )
            }

            is EditorScreenEvent.PickPdfs -> _state.value.copy(
                attachments = _state.value.attachments + event.uris.toEditorAttachmentsPdf()
            )

            is EditorScreenEvent.PickLink -> _state.value.copy(
                attachments = _state.value.attachments + Attachment(
                    url = event.url,
                    fileName = event.url,
                    type = AttachmentType.Link
                )
            )
        }
    }

    @SuppressLint("Range")
    private fun List<Uri>.toEditorAttachmentsPdf(): List<Attachment> =
        map {
            Attachment(
                url = it.toString(),
                fileName = it.getFilename(),
                type = AttachmentType.Pdf
            )
        }


    private fun List<Uri>.toAttachmentsImages(): List<Attachment> =
        this.map {
            val filename = it.getFilename()
            val dimensions = it.getDimensions()

            Attachment(
                fileName = filename,
                type = AttachmentType.Image(dimensions.first, dimensions.second),
                url = it.toString()
            )
        }

    private fun Uri.getFilename(): String {
        val mimeType: String? = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(applicationContext.contentResolver.getType(this))
        val extension = if (mimeType == null) null else ".$mimeType"

        val filename = applicationContext.contentResolver.query(this, null, null, null, null)
            .use { cursor ->
                val index = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    ?: return@use "file-${System.currentTimeMillis()}$extension"
                cursor.moveToFirst()
                cursor.getString(index)
            }

        return filename
    }

    private fun Uri.getDimensions(): Pair<Int, Int> {
        val filename = applicationContext.contentResolver.query(this, null, null, null, null)
            .use { cursor ->
                val widthIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                cursor.moveToFirst()

                Pair(cursor.getInt(widthIndex), cursor.getInt(heightIndex))
            }

        return filename
    }

    private fun delete() {
        viewModelScope.launch {
            // TODO: change it to isDeleting
            _state.value = _state.value.copy(isDeleting = true)
            boardRepository.deleteBoard(board!!)
            _state.value = _state.value.copy(isDeleting = false, isDoneDeleting = true)
        }
    }

    private fun save() {
        val state = _state.value;
        if (state.selectedCategory == null) {
            return;
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            if (boardId == null)
                boardRepository.insert(
                    Board(
                        title = _state.value.titleTextFieldValue.text,
                        attachments = _state.value.attachments,
                        note = _state.value.bodyTextFieldValue.text,
                        category = _state.value.selectedCategory!!,
                        tags = _state.value.selectedTags.toList(),
                        date = LocalDateTime.now()
                    )
                )
            else
                boardRepository.update(
                    Board(
                        id = boardId,
                        title = _state.value.titleTextFieldValue.text,
                        attachments = _state.value.attachments,
                        note = _state.value.bodyTextFieldValue.text,
                        category = _state.value.selectedCategory!!,
                        tags = _state.value.selectedTags.toList(),
                        date = LocalDateTime.now()
                    )
                )

            _state.value = _state.value.copy(isSaving = false, isDone = true)
        }
    }
}


