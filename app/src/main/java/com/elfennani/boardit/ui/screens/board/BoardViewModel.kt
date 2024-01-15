package com.elfennani.boardit.ui.screens.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val boardId = checkNotNull(savedStateHandle.get<String>("id"))

    val state = boardRepository.boards.map { it.find { board -> board.id == boardId } }.map {
        BoardScreenState(it)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BoardScreenState()
    )
}