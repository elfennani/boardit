package com.elfennani.boardit.ui.screens.board

import androidx.compose.runtime.Immutable
import com.elfennani.boardit.data.models.Board

@Immutable
data class BoardScreenState(
    val board: Board? = null,
)
