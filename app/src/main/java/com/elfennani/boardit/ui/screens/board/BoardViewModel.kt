package com.elfennani.boardit.ui.screens.board

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.LinkMetadataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BoardViewModel @Inject constructor(
    boardRepository: BoardRepository,
    savedStateHandle: SavedStateHandle,
    private val linkMetadataRepository: LinkMetadataRepository
) : ViewModel() {
    private val boardId = checkNotNull(savedStateHandle.get<String>("id"))
    private val board = boardRepository.boards.map { it.find { board -> board.id == boardId } }
    private val linksMetadata = linkMetadataRepository.savedMetadata
    private val _state = MutableStateFlow(BoardScreenState())

    val state = combine(board, linksMetadata, _state) { board, linksMetadata, state ->
        state.copy(
            board = board,
            linksMetadata = linksMetadata
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BoardScreenState()
    )

    init {
        viewModelScope.launch {
            board.collectLatest {
                Log.d("LOAD_METADATA", it.toString())
                it?.attachments?.forEach { attachment ->
                    linkMetadataRepository.load(attachment)
                }
            }
        }
    }

    fun setDialogState(dialogState: BoardScreenState.DialogState) {
        _state.value = _state.value.copy(dialogState = dialogState)
    }
}