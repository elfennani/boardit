package com.elfennani.boardit.ui.screens.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    boardRepository: BoardRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val boardId = checkNotNull(savedStateHandle.get<String>("id"))
    private val board = boardRepository.boards.map { it.find { board -> board.id == boardId } }
    private val _state = MutableStateFlow(BoardScreenState())

    val state = combine(board, _state) { board, state ->
        state.copy(board = board)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BoardScreenState()
    )

    fun setDialogState(dialogState: BoardScreenState.DialogState) {
        _state.value = _state.value.copy(dialogState = dialogState)
    }
}