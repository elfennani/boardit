package com.elfennani.boardit.ui.screens.editor

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.EditorAttachment
import com.elfennani.boardit.data.models.toEditorAttachment
import com.elfennani.boardit.data.remote.models.NetworkBoardInsert
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @SuppressLint("StaticFieldLeak")
@Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val boardRepository: BoardRepository,
    private val supabaseClient: SupabaseClient,
    private val savedStateHandle: SavedStateHandle,
//    @ApplicationContext applicationContext: Context
) : ViewModel() {

    private val stateFlow: MutableStateFlow<String> = MutableStateFlow("")

    private val boardId = savedStateHandle.get<String>("id")
    private val board = runBlocking {
        if (boardId != null) boardRepository.boards.first()
            .find { it.id == boardId.toInt() } else null
    }

    private val stateInitialValue = if (board != null)
        EditorScreenState(
            titleTextFieldValue = TextFieldValue(board.title),
            bodyTextFieldValue = TextFieldValue(board.note ?: ""),
            selectedCategory = board.category,
            selectedTags = board.tags.toSet(),
            attachments = board.attachments.map(Attachment::toEditorAttachment),
            isNew = false
        ) else
        EditorScreenState()

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

            is EditorScreenEvent.DeleteAttachment -> _state.value.copy(attachments = _state.value.attachments - event.editorAttachment)

            is EditorScreenEvent.DeleteBoard -> {
                delete()
                _state.value
            }

            is EditorScreenEvent.PickImages -> {
                _state.value.copy(
                    attachments = _state.value.attachments + event.uris.toEditorAttachmentsImages(
                        event.context
                    )
                )
            }

            is EditorScreenEvent.PickPdfs -> _state.value.copy(
                attachments = _state.value.attachments + event.uris.toEditorAttachmentsPdf()
            )

            is EditorScreenEvent.PickLink -> _state.value.copy(
                attachments = _state.value.attachments + EditorAttachment.Local(
                    uri = Uri.parse(event.url),
                    type = AttachmentType.Link
                )
            )
        }
    }

    private fun List<Uri>.toEditorAttachmentsPdf(): List<EditorAttachment> =
        map { EditorAttachment.Local(it, AttachmentType.Pdf) }

    private fun List<Uri>.toEditorAttachmentsImages(context: Context): List<EditorAttachment> =
        this.map {
            val exif = ExifInterface(context.contentResolver.openInputStream(it)!!)
            val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
            val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

            EditorAttachment.Local(
                uri = it,
                type = AttachmentType.Image(width, height),
            )
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
            val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

            if (boardId == null) {
                boardRepository.insert(
                    boardInsert = NetworkBoardInsert(
                        title = state.titleTextFieldValue.text,
                        note = state.bodyTextFieldValue.text,
                        userId = user.id,
                        categoryId = state.selectedCategory.id
                    ),
                    tags = state.selectedTags.toList(),
                    attachments = state.attachments
                )
            } else {
                boardRepository.update(
                    board!!.copy(
                        title = state.titleTextFieldValue.text,
                        note = state.bodyTextFieldValue.text,
                        tags = state.selectedTags.toList(),
                        category = state.selectedCategory
                    ),
                    state.selectedTags.toList() != board.tags,
                    state.attachments,
                    board.attachments.size != state.attachments.filterIsInstance<EditorAttachment.Remote>().size
                )
            }
            _state.value = _state.value.copy(isSaving = false, isDone = true)
        }
    }
}


