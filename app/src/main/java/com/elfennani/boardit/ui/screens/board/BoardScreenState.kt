package com.elfennani.boardit.ui.screens.board

import androidx.compose.runtime.Immutable
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.LinkMetadata

@Immutable
data class BoardScreenState(
    val board: Board? = null,
    val dialogState: DialogState = DialogState.Closed,
    val linksMetadata: List<LinkMetadata> = emptyList()
) {
    sealed interface DialogState {
        data object Closed : DialogState
        data class Open(val initialIndex: Int) : DialogState
    }
}