package com.elfennani.boardit.ui.screens.editor

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.remote.models.NetworkBoardInsert
import com.elfennani.boardit.data.remote.models.NetworkBoardTagsInsert
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val boardRepository: BoardRepository,
    private val supabaseClient: SupabaseClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stateFlow: MutableStateFlow<String> = MutableStateFlow("")

//    private var _state = mutableStateOf(EditorScreenState())
//    val state: State<EditorScreenState> = _state

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
        Log.d(
            "BOARDID",
            savedStateHandle.get<String>("android-support-nav:controller:deepLinkIntent").toString()
        )
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
        }
    }

    fun validate(): Boolean =
        _state.value.selectedCategory != null && _state.value.titleTextFieldValue.text.isNotBlank()

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
                    tags = state.selectedTags.toList()
                )
            } else {
                boardRepository.update(
                    board!!.copy(
                        title = state.titleTextFieldValue.text,
                        note = state.bodyTextFieldValue.text,
                        tags = state.selectedTags.toList(),
                        category = state.selectedCategory
                    ),
                    state.selectedTags.toList() != board.tags
                )
            }
            _state.value = _state.value.copy(isSaving = false, isDone = true)
        }
    }
}